# Optimal Subset Sum with Dynamic Programming

This project demonstrates an efficient solution to the subset sum problem, where for each target value, we select the best possible subset of integers such that their sum is less than or equal to the target and as close as possible to it.

---

## 🧠 Why This Approach is the Best

Dynamic programming (DP) is ideal for the subset sum problem because it guarantees the optimal subset in **polynomial time O(n × target)**. Greedy methods might skip better solutions, and brute-force is exponential. DP avoids redundant computations and ensures **mathematical certainty**, which is essential when precision impacts business KPIs directly.

---

## Advantages

- **Optimality Guaranteed:** Always finds the closest possible sum ≤ target.
- **Efficiency:** O(n × target) runtime makes it scalable and predictable.
- **Memory Efficient:** Uses a boolean array, not all combinations.
- **Robust:** Handles duplicates, zeros, empty inputs.
- **Traceability:** Enables backtracking of selected numbers.

---

## Limitations

- **Large Target Memory Use:** One boolean array of size `target + 1`.
- **Integer Overflow Risk:** For targets beyond ~2.1 billion.
- **Single-Use Constraint:** Each number is used at most once.
- **Integer-Only:** Floating point values must be preprocessed.
- **Infeasibility Beyond BigInteger:** Extremely large targets may need approximation or reformulation.

---

## Output Examples

Below are results for multiple target values, including selected subset, sum achieved, and how close it is to the target.
```
Target: 854 → Selected: [64, 89, 92, 92, 114, 133, 134, 136], Sum: 854, Difference: 0
Target: 277 → Selected: [64, 100, 113], Sum: 277, Difference: 0
Target: 376 → Selected: [23, 48, 72, 84, 149], Sum: 376, Difference: 0
Target: 493 → Selected: [58, 59, 106, 122, 145], Sum: 490, Difference: 3
Target: 554 → Selected: [28, 98, 133, 142, 153], Sum: 554, Difference: 0
Target: 727 → Selected: [50, 62, 83, 110, 134, 139, 149], Sum: 727, Difference: 0
Target: 678 → Selected: [23, 35, 54, 91, 92, 110, 128, 145], Sum: 678, Difference: 0
Target: 239 → Selected: [29, 73, 137], Sum: 239, Difference: 0
Target: 391 → Selected: [65, 92, 114, 120], Sum: 391, Difference: 0
Target: 326 → Selected: [78, 108, 138], Sum: 324, Difference: 2
Target: 817 → Selected: [40, 48, 75, 97, 132, 133, 145, 147], Sum: 817, Difference: 0
Target: 675 → Selected: [52, 86, 114, 126, 143, 152], Sum: 673, Difference: 2
Target: 115 → Selected: [25, 39, 51], Sum: 115, Difference: 0
Target: 552 → Selected: [40, 43, 92, 100, 122, 155], Sum: 552, Difference: 0
Target: 483 → Selected: [35, 70, 76, 87, 99, 115], Sum: 482, Difference: 1
Target: 728 → Selected: [55, 60, 110, 116, 124, 131, 132], Sum: 728, Difference: 0
Target: 739 → Selected: [31, 46, 77, 138, 141, 153, 153], Sum: 739, Difference: 0
Target: 714 → Selected: [27, 61, 88, 97, 100, 109, 115, 117], Sum: 714, Difference: 0
Target: 523 → Selected: [50, 55, 68, 91, 109, 150], Sum: 523, Difference: 0
Target: 746 → Selected: [25, 45, 62, 92, 109, 123, 143, 147], Sum: 746, Difference: 0
Target: 529 → Selected: [27, 29, 71, 80, 92, 108, 120], Sum: 527, Difference: 2
Target: 677 → Selected: [28, 60, 101, 102, 105, 136, 142], Sum: 674, Difference: 3
Target: 424 → Selected: [33, 80, 90, 94, 127], Sum: 424, Difference: 0
Target: 843 → Selected: [24, 55, 57, 78, 97, 98, 134, 146, 154], Sum: 843, Difference: 0
Target: 526 → Selected: [25, 86, 135, 139, 141], Sum: 526, Difference: 0
Target: 178 → Selected: [26, 49, 103], Sum: 178, Difference: 0
Target: 478 → Selected: [40, 46, 53, 68, 124, 147], Sum: 478, Difference: 0
Target: 291 → Selected: [32, 57, 67, 135], Sum: 291, Difference: 0
Target: 264 → Selected: [24, 66, 84, 90], Sum: 264, Difference: 0
Target: 145 → Selected: [38, 39, 67], Sum: 144, Difference: 1
Target: 672 → Selected: [59, 87, 100, 128, 146, 152], Sum: 672, Difference: 0
Target: 774 → Selected: [89, 92, 102, 111, 114, 131, 134], Sum: 773, Difference: 1
Target: 750 → Selected: [34, 50, 64, 67, 67, 97, 105, 110, 155], Sum: 749, Difference: 1
Target: 524 → Selected: [73, 86, 117, 120, 128], Sum: 524, Difference: 0
Target: 374 → Selected: [60, 61, 123, 130], Sum: 374, Difference: 0
Target: 430 → Selected: [24, 87, 92, 94, 133], Sum: 430, Difference: 0
Target: 801 → Selected: [35, 42, 67, 114, 124, 132, 138, 148], Sum: 800, Difference: 1
Target: 985 → Selected: [84, 88, 116, 123, 133, 147, 147, 147], Sum: 985, Difference: 0
Target: 395 → Selected: [24, 86, 131, 154], Sum: 395, Difference: 0
Target: 895 → Selected: [44, 45, 50, 62, 84, 94, 105, 112, 140, 142], Sum: 878, Difference: 17
Target: 873 → Selected: [38, 63, 81, 106, 138, 139, 153, 154], Sum: 872, Difference: 1
Target: 556 → Selected: [25, 38, 38, 56, 60, 102, 112, 125], Sum: 556, Difference: 0
Target: 641 → Selected: [109, 114, 136, 138, 143], Sum: 640, Difference: 1
Target: 474 → Selected: [27, 33, 79, 84, 114, 137], Sum: 474, Difference: 0
Target: 682 → Selected: [43, 78, 135, 137, 142, 147], Sum: 682, Difference: 0
Target: 637 → Selected: [63, 75, 114, 116, 118, 145], Sum: 631, Difference: 6
Target: 437 → Selected: [86, 103, 106, 142], Sum: 437, Difference: 0
Target: 431 → Selected: [23, 75, 89, 112, 132], Sum: 431, Difference: 0
Target: 556 → Selected: [68, 92, 114, 131, 150], Sum: 555, Difference: 1
Target: 738 → Selected: [23, 51, 57, 82, 115, 119, 136, 154], Sum: 737, Difference: 1
Target: 671 → Selected: [47, 78, 93, 95, 112, 112, 132], Sum: 669, Difference: 2
Target: 105 → Selected: [75], Sum: 75, Difference: 30
Target: 236 → Selected: [25, 78, 133], Sum: 236, Difference: 0
Target: 721 → Selected: [92, 101, 109, 135, 137, 147], Sum: 721, Difference: 0
Target: 702 → Selected: [24, 34, 64, 70, 94, 131, 136, 149], Sum: 702, Difference: 0
Target: 182 → Selected: [27, 38, 58, 59], Sum: 182, Difference: 0
Target: 933 → Selected: [27, 74, 83, 92, 109, 122, 131, 145, 150], Sum: 933, Difference: 0
Target: 334 → Selected: [29, 48, 50, 87, 120], Sum: 334, Difference: 0
Target: 992 → Selected: [47, 84, 86, 105, 122, 132, 134, 140, 142], Sum: 992, Difference: 0
Target: 370 → Selected: [54, 90, 90, 136], Sum: 370, Difference: 0
Target: 605 → Selected: [28, 39, 55, 70, 88, 96, 99, 130], Sum: 605, Difference: 0
Target: 428 → Selected: [51, 82, 83, 94, 118], Sum: 428, Difference: 0
Target: 245 → Selected: [116, 129], Sum: 245, Difference: 0
Target: 870 → Selected: [26, 45, 111, 113, 136, 140, 143, 155], Sum: 869, Difference: 1
Target: 356 → Selected: [40, 50, 84, 84, 98], Sum: 356, Difference: 0
Target: 828 → Selected: [23, 63, 70, 83, 92, 115, 125, 127, 130], Sum: 828, Difference: 0
Target: 520 → Selected: [36, 92, 120, 130, 142], Sum: 520, Difference: 0
Target: 393 → Selected: [24, 31, 65, 82, 91, 100], Sum: 393, Difference: 0
Target: 865 → Selected: [57, 67, 92, 99, 122, 122, 151, 155], Sum: 865, Difference: 0
Target: 669 → Selected: [24, 24, 25, 67, 96, 96, 104, 114, 116], Sum: 666, Difference: 3
Target: 734 → Selected: [25, 48, 104, 130, 132, 134, 149], Sum: 722, Difference: 12
Target: 507 → Selected: [27, 28, 56, 74, 82, 110, 130], Sum: 507, Difference: 0
Target: 840 → Selected: [32, 48, 49, 72, 96, 125, 130, 139, 149], Sum: 840, Difference: 0
Target: 779 → Selected: [57, 67, 68, 83, 93, 134, 134, 143], Sum: 779, Difference: 0
Target: 578 → Selected: [29, 78, 81, 103, 142, 145], Sum: 578, Difference: 0
Target: 790 → Selected: [38, 51, 72, 85, 98, 143, 149, 154], Sum: 790, Difference: 0
Target: 405 → Selected: [24, 32, 73, 124, 152], Sum: 405, Difference: 0
Target: 283 → Selected: [136, 147], Sum: 283, Difference: 0
Target: 580 → Selected: [60, 67, 93, 102, 110, 148], Sum: 580, Difference: 0
Target: 651 → Selected: [29, 82, 84, 88, 112, 119, 136], Sum: 650, Difference: 1
```

### Scaling Strategies

To handle large-scale subset sum problems efficiently:

- **Downscale Inputs:**  
  Divide all numbers by a common factor (e.g., 1,000 or 1,000,000), solve the reduced problem, then scale the result back.  
  This trades minimal precision for significant performance gains.

- **Use Approximation for Huge Targets:**  
  For extremely large targets, approximation algorithms or greedy strategies offer "good enough" solutions in a fraction of the time, which is often more practical than waiting hours for exact results.

- **Hybrid Approach:**
    - **Exact DP** for small targets: fast and precise.
    - **Scaled DP** for mid-range: balance between speed and accuracy.
    - **Approximations** for very large targets: prioritize responsiveness.



