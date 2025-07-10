plugins {
    java
}

group = "com.mccarl.es"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://artifacts.elastic.co/maven")
    }
}

dependencies {
    compileOnly("org.elasticsearch:elasticsearch:8.17.0")
    testImplementation("org.elasticsearch.test:framework:8.17.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

// Configure test settings
tasks.test {
    useJUnitPlatform()
    systemProperty("tests.security.manager", "false")
}

// Build configuration
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-serial", "-Werror"))
}

// Task to build plugin ZIP
val buildPluginZip by tasks.creating(Zip::class) {
    group = "build"
    dependsOn("build")

    // Place files directly at the root of the ZIP, no intermediate directory
    from("src/main/resources/plugin-descriptor.properties")
    from("build/libs/${project.name}-${version}.jar")

    archiveFileName.set("${project.name}-${version}.zip")
    destinationDirectory.set(file("$buildDir/distributions"))
}

//tasks.assemble {
//    dependsOn(buildPluginZip)
//}

// Elasticsearch connection configuration
val esHost = project.findProperty("es.host") as String? ?: "localhost"
val esPort = project.findProperty("es.port") as String? ?: "9200"
val esUrl = "http://$esHost:$esPort"

// Task to deploy plugin to running Elasticsearch
tasks.register("deployPlugin") {
    dependsOn(buildPluginZip)
    doLast {
        val pluginZip = file("build/distributions/personalization-plugin-${version}.zip")
        if (!pluginZip.exists()) {
            throw GradleException("Plugin ZIP not found: ${pluginZip.absolutePath}")
        }

        println("Deploying plugin to Elasticsearch at $esUrl")

        // Use shell to ensure Docker is found in PATH
        val isWindows = System.getProperty("os.name").toLowerCase().contains("windows")

        // Copy plugin to Elasticsearch container
        if (isWindows) {
            exec {
                commandLine("cmd", "/c", "docker", "cp", pluginZip.absolutePath, "es-personalization:/tmp/")
            }
        } else {
            exec {
                commandLine("sh", "-c", "docker cp ${pluginZip.absolutePath} es-personalization:/tmp/")
            }
        }

        // Install plugin in Elasticsearch
        if (isWindows) {
            exec {
                commandLine("cmd", "/c", "docker", "exec", "es-personalization",
                    "bin/elasticsearch-plugin", "install", "--batch",
                    "file:///tmp/${pluginZip.name}")
            }
        } else {
            exec {
                commandLine("sh", "-c",
                    "docker exec es-personalization bin/elasticsearch-plugin install --batch file:///tmp/${pluginZip.name}")
            }
        }

        // Restart Elasticsearch to load the plugin
        if (isWindows) {
            exec {
                commandLine("cmd", "/c", "docker", "restart", "es-personalization")
            }
        } else {
            exec {
                commandLine("sh", "-c", "docker restart es-personalization")
            }
        }

        println("Plugin deployed. Waiting for Elasticsearch to restart...")
        Thread.sleep(30000) // Wait 30 seconds for ES to restart
    }
}

// Task to remove plugin from Elasticsearch
tasks.register("removePlugin") {
    doLast {
        println("Removing plugin from Elasticsearch")

        val isWindows = System.getProperty("os.name").toLowerCase().contains("windows")

        if (isWindows) {
            exec {
                commandLine("cmd", "/c", "docker", "exec", "es-personalization",
                    "bin/elasticsearch-plugin", "remove", "--purge", "personalization-plugin")
                isIgnoreExitValue = true
            }
            exec {
                commandLine("cmd", "/c", "docker", "restart", "es-personalization")
            }
        } else {
            exec {
                commandLine("sh", "-c",
                    "docker exec es-personalization bin/elasticsearch-plugin remove --purge personalization-plugin")
                isIgnoreExitValue = true
            }
            exec {
                commandLine("sh", "-c", "docker restart es-personalization")
            }
        }

        println("Plugin removed. Elasticsearch restarting...")
    }
}

// Task to create products index with schema
tasks.register("createProductsIndex") {
    doLast {
        val schemaFile = file("src/main/resources/product_schema.json")
        if (!schemaFile.exists()) {
            throw GradleException("Schema file not found: ${schemaFile.absolutePath}")
        }

        println("Creating products index with schema...")

        // Wait for Elasticsearch to be ready
        exec {
            commandLine("bash", "-c",
                "until curl -s $esUrl/_cat/health?v; do echo 'Waiting for Elasticsearch...'; sleep 5; done")
        }

        // Delete index if it exists
        exec {
            commandLine("curl", "-X", "DELETE", "$esUrl/products")
            isIgnoreExitValue = true
        }

        // Create index with schema
        exec {
            commandLine("curl", "-X", "PUT", "$esUrl/products",
                "-H", "Content-Type: application/json",
                "-d", "@${schemaFile.absolutePath}")
        }

        println("Products index created successfully")
    }
}

// Task to check Elasticsearch health
tasks.register("checkElasticsearch") {
    doLast {
        exec {
            commandLine("curl", "-s", "$esUrl/_cat/health?v")
        }
        exec {
            commandLine("curl", "-s", "$esUrl/_cat/plugins?v")
        }
    }
}

// Combined task to deploy plugin and create index
tasks.register("setupElasticsearch") {
    dependsOn("deployPlugin", "createProductsIndex")
    tasks["createProductsIndex"].mustRunAfter(tasks["deployPlugin"])
}