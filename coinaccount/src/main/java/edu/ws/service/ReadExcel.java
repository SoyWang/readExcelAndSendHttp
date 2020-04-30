package edu.ws.service;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangSong
 * @apiNote 读取excel文件的内容：
 *  1、遍历出姓名列；
 *  2、取出研发部所有人员姓名
 *  3、将中文的姓名转换成英文；
 *  4、调用区块链创建币账户接口创建所有用户的账户；
 */
public class ReadExcel {
    private static String excelPath = "C:\\Users\\戴尔\\Desktop\\公司人员联系方式.xls";
    private static List<String> employees = new ArrayList<String>();//读取出excel的员工姓名
    private static List<String> departments = new ArrayList<String>();//读取出excel的部门
    private static Map<String, String> researchMap = new ConcurrentHashMap<String, String>();//只有研发部的人员map
    private static String url = "http://gateway.das.sunsheen.cn/api/rest/das/hearken-coin/init-account";

    public static void main(String[] args) {
        try {
            //只需要研发部的人员姓名
            readColumn(new File(excelPath), 4, true);//读取姓名：4为第五列---姓名列
            readColumn(new File(excelPath), 18, false);//读取部门
            //员工跟部门个数是不是一致
            System.out.println(employees.size());
            System.out.println(departments.size());
            //得到只有研发部人员名单的员工
            if (employees.size() == departments.size()) {
                readResearch(employees, departments);
            } else {
                System.out.println("部门跟员工数据不一致！");
            }
            //将研发部所有人员名字转成拼音
            List<String> researchUsers = transferPinyin(researchMap);
            //发送http请求创建人员名字
//            RequestThread.run(url,researchUsers);
            researchUsers.clear();
            researchMap.forEach((k,v)->{
                researchUsers.add(k);
            });
            RequestThread.run(url,researchUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //名字转成拼音
    private static List<String> transferPinyin(Map<String, String> researchMap) {
        List<String> names = new ArrayList<>();
        researchMap.forEach((k, v) -> {
            try {
                String name = getPingYin(k);
//                System.out.println(name);
                if (null !=name && !"".equals(name))
                    names.add(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return names;
    }

    //将字符串中的中文转化为拼音,英文字符不变
    private static String getPingYin(String inputString) throws Exception {
        //名字中间不分割：xiaoming
        String tempStr =  PinyinHelper.convertToPinyinString(inputString, "", PinyinFormat.WITHOUT_TONE);
        return tempStr;
    }

    //读取文件执行
    private static void readResearch(List<String> employees,List<String> departments) throws Exception{
        //所有信息存入
        AtomicInteger i = new AtomicInteger(0);
        employees.forEach(employee -> {
            researchMap.put(employee,departments.get(i.get()));
            i.getAndIncrement();
        });
        //移除不是研发部的员工信息----------> 如果不是并发安全的map，需要使用迭代器移除map数据，不然会报错
//        Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();
//        while(it.hasNext()){
//            Map.Entry<Integer, String> entry = it.next();
//            Integer key = entry.getKey();
//            if(key % 2 == 0){
//                System.out.println("To delete key " + key);
//                it.remove();
//                System.out.println("The key " + + key + " was deleted");
//            }
//        }
        System.out.println(researchMap.size());
        for (Map.Entry<String, String> entry : researchMap.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (!v.contains("研发") || k.contains("管理员")) {
                researchMap.remove(k);
                continue;
            }
            researchMap.put("张殿超","研发部");
//            System.out.println(k +"："+ v);
        }
    }

    /**
     * 读取excel表格中特定的列
     * @param file  文件
     * @param index 第index列（0开始）
     * @throws Exception
     */
    public static void readColumn(File file, int index ,boolean readEmp) throws Exception {
        //文件流
        InputStream inputStream = new FileInputStream(file.getAbsoluteFile());
        //工作簿
        Workbook workbook = Workbook.getWorkbook(inputStream);
        //单元格
        Sheet sheet = workbook.getSheet(0);
        int rows = sheet.getRows();
        int columns = sheet.getColumns();
        for (int i = 1; i < rows; i++) {
            Cell cell = sheet.getCell(index, i);
            String name = cell.getContents();
            //读取员工
            if (readEmp){
                //去除姓名中的数字
                String employee = splitNotNumber(name);
                employees.add(employee);
            }
            //读取部门
            else
                departments.add(name);
        }
    }


    // 截取非数字
    private static String splitNotNumber(String content) {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    // 判断一个字符串是否含有数字
    private static boolean HasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }

}
