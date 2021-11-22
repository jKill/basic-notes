package haiwaitu.t20211121;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author huangjunqiao
 * @Date 2021/11/22 22:57
 * @Description 384. 打乱数组
 */
public class Solution {
    int[] original;
    // 暴力，用时：21min
    public Solution(int[] nums) {
        // 时间：O(N)，空间：O(N)
        this.original = new int[nums.length];
        System.arraycopy(nums, 0, original, 0, nums.length);
    }

    public int[] reset() {
        // 时间：O(N)，空间：O(N)
        int[] nums = new int[original.length];
        System.arraycopy(original, 0, nums, 0, original.length);
        return nums;
    }

    public int[] shuffle() {
        // 时间：O(N^2)，空间：O(N)
        int len = original.length;
        int[] shuffle = new int[len];
        List<Integer> list = new ArrayList<>();
        for (int num : original) {
            list.add(num);
        }
        Random rand = new Random();
        for (int i = 0; i < len; i++) {
            int j = rand.nextInt(list.size());
            shuffle[i] = list.remove(j);
        }
        return shuffle;
    }

//    int[] nums;
//    // Fisher-Yates 洗牌算法
//    public Solution(int[] nums) {
//        this.nums = nums;
//        this.original = new int[nums.length];
//        System.arraycopy(nums, 0, original, 0, nums.length);
//    }
//
//    public int[] reset() {
//        System.arraycopy(original, 0, nums, 0, nums.length);
//        return nums;
//    }
//
//    public int[] shuffle() {
//        int len = original.length;
//        Random rand = new Random();
//        for (int i = 0; i < len; i++) {
//            int j = i + rand.nextInt(len - i);
//            int temp = nums[i];
//            nums[i] = nums[j];
//            nums[j] = temp;
//        }
//        return nums;
//    }
}
