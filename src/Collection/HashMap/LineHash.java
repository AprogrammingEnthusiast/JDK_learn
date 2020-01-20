/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package Collection.HashMap;

/**
 * 开放地址法 -- 线性探测
 * @author wb-wj449816
 * @version $Id: LineHash.java, v 0.1 2020年01月20日 16:03 wb-wj449816 Exp $
 */
public class LineHash {

    private DataItem[] store;
    private int        maxSize;
    private int        size;

    private LineHash(int maxSize) {
        this.maxSize = maxSize;
        store = new DataItem[maxSize];
        size = 0;
    }

    /**
     * 哈希化 函数
     * @param hashCode
     * @return
     */
    private int hashFunc(int hashCode) {
        return hashCode % maxSize;
    }

    private void insert(DataItem value) {
        int hashCode = value.hashCode();
        // 得到哈希化之后的值
        int hashIndex = hashFunc(hashCode);
        // hashIndex对应坐标的不为null，且没有被删除，那么就查找下一个元素。
        // 直到找到hashIndex对应位置 元素 为空，或者被删除。那么就在这个位置插入新元素。
        // 注意这里没有考虑 死循环的情况
        while (store[hashIndex] != null && store[hashIndex] != DataItem.DELETE_ITEM) {
            hashIndex = hashIndex + 1;
            if (hashIndex == maxSize) { hashIndex = 0; }
        }
        store[hashIndex] = value;
        size++;
    }

    private DataItem get(int hashCode) {
        // 得到哈希化之后的值
        int hashIndex = hashFunc(hashCode);
        // 跳出循环的条件有两个，一个是找到一个空白元素，表示没有查到对应元素，二是找到相同hashCode的值，返回它。
        // 注意不能将被删除元素作为循环终止条件,被删除元素只能作为继续查找下一个元素的条件
        while (store[hashIndex] != null) {
            // 如果hashIndex对应位置元素被删除了，那么就查找下一个元素。
            // 注意这里千万不能将store[hashIndex] != DataItem.deleteItem判断条件放到while循环中。
            if (store[hashIndex] != DataItem.DELETE_ITEM) {
                DataItem temp = store[hashIndex];
                if (temp.hashCode() == hashCode) {
                    return temp;
                }
            }

            hashIndex = hashIndex + 1;
            if (hashIndex == maxSize) { hashIndex = 0; }
        }
        return null;
    }

    private DataItem remove(int hashCode) {
        int hashIndex = hashFunc(hashCode);
        // 跳出循环的条件有两个，一个是找到一个空白元素，表示没有查到对应元素，
        // 二是找到相同hashCode的值，就将hashIndex对应下标元素换成deleteItem，表示已删除
        // 注意不能将被删除元素作为循环终止条件,被删除元素只能作为继续查找下一个元素的条件
        while (store[hashIndex] != null) {
            // 如果hashIndex对应位置元素被删除了，那么就查找下一个元素。
            // 注意这里千万不能将store[hashIndex] != DataItem.deleteItem判断条件放到while循环中。
            if (store[hashIndex] != DataItem.DELETE_ITEM) {
                DataItem temp = store[hashIndex];
                if (temp.hashCode() == hashCode) {
                    size--;
                    store[hashIndex] = DataItem.DELETE_ITEM;
                    return temp;
                }
            }
            hashIndex = hashIndex + 1;
            if (hashIndex == maxSize) { hashIndex = 0; }
        }

        return null;
    }

    private int getSize() {
        return size;
    }

    public static void main(String[] args) {

        LineHash lineHash = new LineHash(333);
        for (int i = 0; i < 100; i++) {

            DataItem dataItem = new DataItem(i + "_" + i);
            lineHash.insert(dataItem);
        }

        System.out.println("size === " + lineHash.getSize());

        DataItem keyDataItem1 = new DataItem("50_50");
        DataItem keyDataItem2 = new DataItem("25_25");
        // 查询方法是否有效
        System.out.println("get() ==" + lineHash.get(keyDataItem1.hashCode()));
        System.out.println("get() ==" + lineHash.get(keyDataItem2.hashCode()));

        // 删除方法是否有效
        System.out.println("remove == " + lineHash.remove(keyDataItem1.hashCode()));
        // 重复删除不会有异常
        System.out.println("remove == " + lineHash.remove(keyDataItem1.hashCode()));

        // 删除之后，在哈希表中就查找不到了
        System.out.println("get() ==" + lineHash.get(keyDataItem1.hashCode()));
        // 删除不会影响其他元素。
        System.out.println("get() ==" + lineHash.get(keyDataItem2.hashCode()));
        System.out.println("get() ==" + lineHash.get(new DataItem("75_75").hashCode()));
        System.out.println("get() ==" + lineHash.get(new DataItem("755").hashCode()));
        System.out.println("size === " + lineHash.getSize());
    }
}

class DataItem {
    private String data;

    /** 用deleteItem这个静态变量标记已删除元素。因为在链表删除的时候，是不能将数组中的元素置位null，用这个标记 */
    static final DataItem DELETE_ITEM = new DataItem();

    DataItem() {
    }

    DataItem(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DataItem)) {
            return false;
        }
        DataItem other = (DataItem) obj;
        return this.data.equals(other.data);
    }

    /** 用来缓存hash值，避免每次都计算hashCode值 */
    private int hash;

    /**
     * 注意这里计算hashCode方法，很容易就数值溢出了，即超出int类型的最大值。
     */
    @Override
    public int hashCode() {
        if (hash != 0) {
            return hash;
        }
        char[] chars = data.toCharArray();
        int h = 0;

        for (char ch : chars) {
            h = h * 31 + ch;
        }
        hash = h;
        return h;
    }

    @Override
    public String toString() {
        return " {" + "data=" + data
               + '}';
    }
}