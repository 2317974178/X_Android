package com.qdreamer.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XJun
 * @date 2023/12/1 11:20
 **/
public class SkipChannel {


    /**
     * 跳音频通道
     * @param data PCM格式的音频数据
     * @param totalChannel 总通道数
     * @param indexArray 要选择的通道数组
     * @return 跳好后的音频数据
     */
    public static byte[] skipChannel(byte[] data,int totalChannel,int[] indexArray){
        List<byte[]> dataList = new ArrayList<>();
        //根据通道初始化dataList
        initDataList(dataList, totalChannel, data.length / totalChannel);

        for (int i = 0; i < data.length; i += totalChannel*2) {
            int index = i / (totalChannel*2);
            for (int j = 0; j < dataList.size(); j++) {
                dataList.get(j)[2*index] = data[i + j*2];
                dataList.get(j)[2*index + 1] = data[i + 1 + j*2];
            }
        }
       return mergeChannel(indexArray,data.length/totalChannel*indexArray.length,dataList);
    }

    /**
     * 合并音频通道数据
     * @param index 要合并的通道数组
     * @param dataLength 要合并的数据总长度
     * @param dataList 要合并的数据
     * @return
     */
    private static byte[] mergeChannel(int[] index,int dataLength,List<byte[]> dataList){
        List<byte[]> pendingData = new ArrayList<>();
        //要返回的数据总长度
        byte[] data = new byte[dataLength];
        for (int i:index) {
            pendingData.add(dataList.get(i));
        }
        //合并数据
        for (int i = 0; i < data.length; i += pendingData.size()*2) {
            for (int j = 0; j < pendingData.size(); j++) {
                data[i + j*2] = pendingData.get(j)[i/pendingData.size()];
                data[i + j*2 + 1] = pendingData.get(j)[i/pendingData.size()+1];
            }
        }
        return data;
    }

    /**
     * 初始化dataList
     * @param dataList 要初始化的dataList
     * @param size dataList的大小
     * @param dataLength 每个byte[] 的长度
     */
    private static void initDataList(List<byte[]> dataList, int size, int dataLength){
        for (int i = 0; i < size; i++) {
            byte[] data = new byte[dataLength];
            dataList.add(data);
        }
    }
}
