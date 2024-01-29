package com.qdreamer.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 执行shell脚本工具类
 * @author Mountain
 *
 */
public class CommandExecution {

    public static final String TAG = "CommandExecution";
    public final static String COMMAND_SU       = "su";
    public final static String COMMAND_SH       = "sh";
    public final static String COMMAND_EXIT     = "exit\n";
    public final static String COMMAND_LINE_END = "\n";

    // 获取ROOT权限
    public static void get_root(Context context){
        if (is_root()){
            Toast.makeText(context, "已经具有ROOT权限!", Toast.LENGTH_SHORT).show();
        }

        else{
            try{
                //progress_dialog = ProgressDialog.show(context,"ROOT", "正在获取ROOT权限...", true, false);
                Runtime.getRuntime().exec("/system/xbin/su");
            }
            catch (Exception e){
                Toast.makeText(context, "获取ROOT权限时出错!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    // 判断是否具有ROOT权限
    public static boolean is_root() {
        boolean res = false;
        try {
            res = (new File("/system/bin/su").exists()) ||
                    (new File("/system/xbin/su").exists());
            ;
        } catch (Exception e) {
        }
        return res;
    }

    /**
     * Command执行结果
     * @author Mountain
     *
     */
    public static class CommandResult {
        public int result = -1;
        public String errorMsg;
        public String successMsg;
    }

    /**
     * 执行命令—单条
     * @param command
     * @param isRoot
     * @return
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        String[] commands = {command};
        return execCommand(commands, isRoot);
    }





    public static void dxCommand(String command) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);
        process.waitFor();
        if(process.exitValue()!=0){
            InputStream inputStream = process.getErrorStream();
            byte[] buffer = new byte[2048];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while (inputStream.read(buffer)!=-1){
                bos.write(buffer);
            }
            Log.d(TAG, "dxCommand: " + bos);
        }
        process.destroy();
    }

    public static int getCardNumber(String cardName){
        String result = executeShellCommand("cat /proc/asound/cards");
        String[] cards = result.split("\n");
        for (int i = 0; i < cards.length; i++) {
            if(cards[i].equals(cardName)){
                return i+1;
            }
        }
        return -1;
    }


    public static String executeShellCommand(String command){
        StringBuilder output = new StringBuilder();
        Process process;
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                output.append(line).append("\n");
            }
            reader.close();
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
        return output.toString();
    }

    /**
     * 执行命令-多条
     * @param commands
     * @param isRoot
     * @return
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        CommandResult commandResult = new CommandResult();
        if (commands == null || commands.length == 0) return commandResult;
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            process = Runtime.getRuntime().exec(COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command != null) {
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
                }
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            commandResult.result = process.waitFor();
            //获取错误信息
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) successMsg.append(s);
            while ((s = errorResult.readLine()) != null) errorMsg.append(s);
            commandResult.successMsg = successMsg.toString();
            commandResult.errorMsg = errorMsg.toString();
            Log.i(TAG, commandResult.result + " | " + commandResult.successMsg
                    + " | " + commandResult.errorMsg);
        } catch (Exception e) {
            String errmsg = e.getMessage();
            if (errmsg != null) {
                Log.e(TAG, errmsg);
            } else {
                e.printStackTrace();
            }
        } finally {
            try {
                if (os != null) os.close();
                if (successResult != null) successResult.close();
                if (errorResult != null) errorResult.close();
            } catch (IOException e) {
                String errmsg = e.getMessage();
                if (errmsg != null) {
                    Log.e(TAG, errmsg);
                } else {
                    e.printStackTrace();
                }
            }
            if (process != null) process.destroy();
        }
        return commandResult;
    }
}
