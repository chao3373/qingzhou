package com.shenke.controller.admin;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.annotation.Resource;

import com.shenke.entity.*;
import com.shenke.repository.SaleListProductRepository;
import com.shenke.service.*;
import com.shenke.util.StringUtil;
import org.omg.CORBA.ObjectHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 入库单Controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/admin/storage/")
public class StorageAdminController {

    @Resource
    private StorageService storageService;

    @Resource
    private SaleListProductService saleListProductService;

    @Resource
    private ClerkService clerkService;

    @Resource
    private GroupService groupService;

    @Resource
    private LogService logService;

    @Resource
    private SaleListProductRepository saleListProductRepository;

    /**
     * 入库
     *
     * @return
     */
    @RequestMapping("/add")
    public Map<String, Object> add(Storage storage, String clerkName, String groupName, Double changdu, String type) {
        System.out.println(storage);
        System.out.println("员工名称：" + clerkName);
        if (changdu != null) {
            storageService.add(storage, clerkName, groupName, changdu, type);
        } else {
            storageService.add(storage, clerkName, groupName);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Integer id = storageService.selectByMaxId().getId();
        map.put("success", true);
        map.put("id", id);
        return map;
    }

    /**
     * 非标入库
     *
     * @return
     */
    @RequestMapping("/feibiaoAdd")
    public Map<String, Object> feibiaoAdd(Storage storage, String clerkName, String groupName) {
        storageService.feibiaoAdd(storage, clerkName, groupName);
        Map<String, Object> map = new HashMap<String, Object>();
        Integer id = storageService.selectByMaxId().getId();
        map.put("success", true);
        map.put("id", id);
        return map;
    }

    /**
     * 根据订单销售商品id查询入库单
     *
     * @return
     */
    @RequestMapping("/selectByMaxId")
    public Map<String, Object> selectByMaxId() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        map.put("row", storageService.selectByMaxId());
        return map;
    }

    /**
     * 数据库改为提货
     *
     * @param ids
     * @return
     */

    @RequestMapping("/out")
    public Map<String, Object> outStorage(Integer[] ids, String pandianji) {
        Map<String, Object> map = new HashMap<>();
        storageService.out(ids, new Date(), pandianji);
        logService.save(new Log(Log.UPDATE_ACTION, "提货：" + "数量：" + ids.length + " id：" + Arrays.toString(ids) + "盘点机：" + pandianji));
        map.put("success", true);
        return map;
    }

    /**
     * 手动出库
     *
     * @Description:
     * @Param:
     * @return:
     * @Author: Andy
     * @Date:
     */
    @RequestMapping("/outKu")
    public Map<String, Object> out(String ids) throws Exception {
        String ck = storageService.genCode();
        Map<String, Object> map = new HashMap<>();
        String[] idArr = ids.split(",");
        storageService.updateStateById("装车", idArr, new Date(), ck);
        map.put("success", true);
        return map;
    }

    /**
     * 查询所有的商品
     */
    @RequestMapping("/outSuccess")
    public Map<String, Object> outSuccess() {
        Map<String, Object> map = new HashMap<>();
        map.put("rows", storageService.outSuccess());
        return map;
    }

    /***
     * 根据id修改盘点机序列号
     * @param pandianji
     * @param ids
     * @return
     */
    @RequestMapping("/save")
    public Map<String, Object> gai(String pandianji, Integer[] ids) {
        Map<String, Object> map = new HashMap<>();
        logService.save(new Log(Log.AUDIT_ACTION, "准备出库" + ids.length));
        storageService.updatePanDianJiByIds(pandianji, ids);
        map.put("success", true);
        return map;
    }

    /**
     * 根据客户名称查询并按产品名称分组
     *
     * @Description:
     * @Param:
     * @return:
     * @Author: Andy
     * @Date:
     */
    @RequestMapping("/findByClientAndGroupByName")
    public Map<String, Object> findByClientAndGroupByName(String client) {
        System.out.println(client);
        Map<String, Object> map = new HashMap<String, Object>();
        List<Object[]> byClientAndGroupByName = storageService.findByClientAndGroupByName(client);
        map.put("success", true);
        map.put("data", byClientAndGroupByName);
        return map;
    }

    /**
     * 根据id查询
     *
     * @Description:
     * @Param:
     * @return:
     * @Author: Andy
     * @Date:
     */
    @RequestMapping("/findById")
    public Map<String, Object> findById(Integer id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", storageService.findById(id));
        return map;
    }

    @RequestMapping("/findbygroup")
    public Map<String, Object> FindByGroup(String client) {
        Map<String, Object> map = new HashMap<>();
        List<JieSuan> findbygroup = storageService.FindByGroup(client);
        map.put("success", true);
        map.put("data", findbygroup);

        return map;
    }

    /**
     * 根据条件查询提货状态的订单
     *
     * @Description:
     * @Param:
     * @return:
     * @Author: Andy
     * @Date:
     */
    @RequestMapping("/searchLiftMoney")
    public Map<String, Object> searchLiftMoney(String saleNumber, String name, String client, String mode, String price, String realityweight, String productDate, String productDatee, String peasant) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map1 = new HashMap<>();
        System.out.println(productDate);
        System.out.println(productDatee);
        map1.put("saleNumber", saleNumber);
        map1.put("peasant", peasant);
        map1.put("productDate", productDate);
        map1.put("productDatee", productDatee);
        map1.put("realityweight", realityweight);
        map1.put("name", name);
        map1.put("client", client);
        map1.put("mode", mode);
        map1.put("price", price);
        map.put("success", true);
        map.put("rows", storageService.searchLiftMoney(map1));
        return map;
    }

    /**
     * 根据id设置仓位信息
     *
     * @Description:
     * @Param:
     * @return:
     * @Author: Andy
     * @Date:
     */
    @RequestMapping("/setLocation")
    public Map<String, Object> setLocation(String ids, Integer location) {
        Map<String, Object> map = new HashMap<>();
        String[] split = ids.split(",");
        for (int i = 0; i < split.length; i++) {
            storageService.setLocation(Integer.parseInt(split[i]), location);
        }
        map.put("success", true);
        return map;
    }

    /**
     * 手动添加库存信息
     *
     * @Description:
     * @Param:
     * @return:
     * @Author: Andy
     * @Date:
     */
    @RequestMapping("/saveAdd")
    public Map<String, Object> saveAdd(Storage storage) {
        storage.setDabaonum(1);
        storage.setPeasant("");
        storageService.save(storage);
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        return map;
    }

    /**
     * 查询所有已经装车的商品
     *
     * @Description:
     * @Param:
     * @return:
     * @Author: Andy
     * @Date:
     */
    @RequestMapping("/truck")
    public Map<String, Object> truck(String state) {
        Map<String, Object> map = new HashMap<>();
        if (state == null) {
            state = "%%";
        } else {
            state = "%" + state + "%";
        }
        map.put("rows", storageService.findByState(state));
        return map;
    }

    /**
     * 按条件查询出库明细表
     *
     * @Description:
     * @Param:
     * @return:
     * @Author: Andy
     * @Date:
     */
    @RequestMapping("/detail")
    public Map<String, Object> detail(String date, String client, String peasant, String product, String chukudanhao, String order) throws ParseException {
        System.out.println("出库单号：" + chukudanhao);
        System.out.println(date);
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map1 = new HashMap<>();
        if (StringUtil.isNotEmpty(date)) {
            map1.put("date", date);
        } else {
            map1.put("date", null);
        }
        map1.put("client", client);
        map1.put("peasant", peasant);
        map1.put("product", product);
        map1.put("order", order);
        map1.put("chukudanhao", chukudanhao);
        System.out.println(map1.get("chukudanhao"));
//        List<Storage> storageList = storageService.detail(map1);
        Map<String, Object> storageList = storageService.detaill(map1);
        return storageList;
//        for (Storage storage : storageList) {
//            Integer integer = storageService.countByDetail(storage, (String) map1.get("date"));
//            System.out.println(integer);
//            storage.setSum(integer);
//            Double danjian = storage.getDabaonum() * storage.getRealityweight();
//            Double danjianpingfang = storage.getDabaonum() * storage.getPingfang();
//            System.out.println(storage);
//            Double unitPrice = storage.getUnitPrice();
//            Double price = storage.getUnitPrice();
//            if (price == null) {
//                price = 0.0;
//            }
//            storage.setUnitPrice(storage.getDabaonum() * price);
//            Double totalPrice = storage.getUnitPrice() * storage.getNum();
//            storage.setDanjianpingfang(danjianpingfang);
//            storage.setDanjianzhong(danjian);
//            Double zongzhong = storage.getSum() * danjian;
//            Double zongpingfang = storage.getSum() * danjianpingfang;
//            System.out.println("总数量：" + integer);
//            System.out.println("打包数：" + storage.getDabaonum());
//            System.out.println("单件重量：" + danjian);
//            System.out.println("总重量：" + zongzhong);
//            storage.setZongpingfang(new BigDecimal(zongpingfang).setScale(2, BigDecimal.ROUND_UP).doubleValue());
//            storage.setZongzhong((new BigDecimal(zongzhong).setScale(2, BigDecimal.ROUND_UP).doubleValue()));
//            storage.setTotalPrice(new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_UP).doubleValue());
//        }
//        map.put("success", true);
//        map.put("rows", storageList);
    }

    /**
     * 根据出库时间获取客户名称
     */
    @RequestMapping("/selectClientNameByOutDate")
    public List<Storage> selectClientNameByOutDate(String outDate) throws ParseException {
        return storageService.selectClientNameByOutDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(outDate).getTime()));
    }

    /**
     * 根据出库单号查询
     */
    @RequestMapping("/selectOutByOutNumber")
    public List<Storage> selectOutByOutNumber(String outNumber) {
        System.out.println(outNumber);
        return storageService.selectOutByOutNumber(outNumber);
    }

    /***
     * 根据商品名称和出库单号查询数量
     * @param name
     * @param outNumber
     * @return
     */
    @RequestMapping("/selectCountByNameAndOutNumber")
    public String selectCountByNameAndOutNumber(String name, String outNumber) {
        return storageService.selectCountByNameAndOutNumber(name, outNumber);
    }

    /***
     * 根据Salelist
     * @param
     * @return
     */
    @RequestMapping("/findbySalelistId")
    public Map<String, Object> FindBySaleListId() {
        Map<String, Object> map = new HashMap<>();
        List<Count> findBySaleListId = storageService.FindBySaleListId();
        map.put("success", true);
        map.put("rows", findBySaleListId);

        System.out.println(map);
        return map;
    }

    /***
     * 查询所有的库存
     * @return
     */
    @RequestMapping("/findAll")
    public Map<String, Object> findAll() {
        Map<String, Object> map = new HashMap<>();
        List<Storage> list = storageService.findAll();
        map.put("rows", list);
        return map;
    }

    @RequestMapping("/findSaleListNumber1")
    public Map<String, Object> findSaleListNumber() {
        Map<String, Object> map = new HashMap<>();
        List<Storage> list = storageService.findSaleListNumber();
        map.put("rows", list);
        return map;
    }

    @RequestMapping("/findSaleListId")
    public Map<String, Object> findStorage(String saleNumber) {
        Map<String, Object> map = new HashMap<>();
        List<Storage> list1 = storageService.findStorage(saleNumber);
        map.put("rows", list1);
        return map;
    }

    /***
     * 查询每个班组的生产产量
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping("/JitaiProduct")
    public Map<String, Object> JitaiProduct(Storage storage, String dateInProducedd) {
        System.out.println(storage);
        System.out.println(dateInProducedd);
        Map<String, Object> map = new HashMap<>();
        System.out.println(storage.getGroupName());
        if (StringUtil.isNotEmpty(storage.getGroupName()) && storage.getGroupName().equals("夜班")) {
            String star = dateInProducedd + " 19:00:00";
            String end = dateInProducedd.split("-")[0] + "-" + dateInProducedd.split("-")[1] + "-" + (Integer.parseInt(dateInProducedd.split("-")[2]) + 1) + " 06:59:59";
            try {
                java.util.Date stard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(star);
                java.util.Date endd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end);
                System.out.println("夜班");
                System.out.println(stard);
                System.out.println(endd);
                map.put("rows", storageService.JitaiProduct(storage, null, stard, endd));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (StringUtil.isNotEmpty(storage.getGroupName()) && storage.getGroupName().equals("白班")) {
            String star = dateInProducedd + " 07:00:00";
            String end = dateInProducedd +  " 18:59:59";
            try {
                java.util.Date stard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(star);
                java.util.Date endd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end);
                System.out.println("白班");
                System.out.println(stard);
                System.out.println(endd);
                map.put("rows", storageService.JitaiProduct(storage, null, stard, endd));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            try {
                java.util.Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 00:00:00");
                String end = dateInProducedd + " 23:59:59";
                java.util.Date endd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end);
                map.put("rows", storageService.JitaiProduct(storage, date, date, endd));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        map.put("success", true);
        return map;
    }

    @RequestMapping("/kucunzonglan")
    public Map<String, Object> KuCunZongLan(String clientname, String saleNumber, String saleDate) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("clientname", clientname);
        map1.put("saleNumber", saleNumber);
        map1.put("saleDate", saleDate);

        map.put("success", true);
        map.put("rows", storageService.KucunSearch(map1));
        return map;
    }

    /***
     * 设置仓位页面查询
     * @return
     */
    @RequestMapping("/select")
    public Map<String, Object> select(Storage storage, String dateInProducedd) {
        if (storage.getGroup() != null) {
            storage.setGroupName(groupService.findById(storage.getGroup().getId()).getName());
        }
        Map<String, Object> map = new HashMap<>();
        List<Storage> list = storageService.select(storage, dateInProducedd);
        map.put("success", true);
        map.put("rows", list);
        return map;
    }

    /***
     * 修改库存页面查询
     * @param storage
     * @param dateInProducedd
     * @return
     */
    @RequestMapping("/selectEdit")
    public Map<String, Object> selectEdit(Storage storage, String dateInProducedd, Integer page, Integer rows) {
        if (storage.getGroup() != null) {
            storage.setGroupName(groupService.findById(storage.getGroup().getId()).getName());
        }
        return storageService.selectEditt(storage, dateInProducedd, page, rows);
    }

    /***
     * 当前库存查询页面查询
     * @return
     */
    @RequestMapping("/findKuCun")
    public Map<String, Object> findKuCun(Storage storage, String dateInProducedd, String dateInProduceddd, Integer page, Integer rows) {
        System.out.println(storage);
        System.out.println(rows);
        if (storage.getGroup() != null) {
            storage.setGroupName(groupService.findById(storage.getGroup().getId()).getName());
        }
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> results = storageService.selecttt(storage, dateInProducedd, dateInProduceddd, page, rows);
        return results;
    }

    /***
     * 修改时间
     * @param ids
     * @param date
     * @return
     */
    @RequestMapping("/updateshijian")
    public String updateshijian(Integer[] ids, String date) {
        try {
            Date parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
            for (int i = 0; i < ids.length; i++) {
                storageService.updateshijian(ids[i], parse);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "修改成功";
    }

    /***
     *
     * 根据ids和状态设置状态
     * @param idArr
     * @param state
     * @return
     */
    @RequestMapping("/updateByIdsAndState")
    public Map<String, Object> updateByIdAndState(String idArr, String state) {
        Map<String, Object> map = new HashMap<>();
        String[] split = idArr.split(",");
        storageService.updateByIdAndState(split, state);
        map.put("success", true);
        return map;
    }

    /***
     * 根据状态查询
     * @param state
     * @return
     */
    @RequestMapping("/selectByState")
    public Map<String, Object> selectByState(String state) {
        System.out.println(state);
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("rows", storageService.selectByState(state));
        return map;
    }

    @RequestMapping("/selectState")
    public Map<String, Object> selectState(String state) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("rows", storageService.selectByState(state));
        return map;
    }

    /***
     * 选中的商品回滚到仓库
     * @param ids
     * @return
     */
    @RequestMapping("/goBackku")
    public Map<String, Object> goBackku(String ids) {
        Map<String, Object> map = new HashMap<>();
        String[] split = ids.split(",");
        storageService.updateByIdAndState(split, "生产完成");
//        for (int i = 0; i < split.length; i++) {
//            Storage storage = storageService.findById(Integer.parseInt(split[i]));
//
//        }
        map.put("success", true);
        return map;
    }

    /***
     * 查询所有生产完成的
     * @return
     */
    @RequestMapping("/allWanCheng")
    public Map<String, Object> allWanCheng() {
        Map<String, Object> map = new HashMap<>();
        List<Storage> byState = storageService.findByState("%生产完成%");
        map.put("success", true);
        map.put("rows", byState);
        return map;
    }

    /***
     * 修改生产员工
     * @param ids
     * @param clerkName
     * @return
     */
    @RequestMapping("/updateClerk")
    public Map<String, Object> updateClerk(Integer[] ids, String clerkName) {
        Integer clerkId = clerkService.finName(clerkName).getId();
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < ids.length; i++) {
            storageService.updateClerk(ids[i], clerkName, clerkId);
        }

        map.put("success", true);
        return map;
    }

    /***
     * 按月查询报表
     * @param month
     * @param year
     * @return
     */
    @RequestMapping("/selectMonth")
    public Map<String, Object> selectMonth(String month, String year) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        List<Month> list = new ArrayList<>();
        list.add(storageService.selectMonth(month, year));
        map.put("rows", list);
        return map;
    }

    /***
     * 按年查询报表
     * @param year
     * @return
     */
    @RequestMapping("/selectYear")
    public Map<String, Object> selectYear(String year) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        List<Month> list = new ArrayList<>();
        list.add(storageService.selectYear(year));
        map.put("rows", list);
        return map;
    }

    @RequestMapping("/updatebanzu")
    public Map<String, Object> updatebanzu(Integer[] ids, String banzu) {
        Map<String, Object> map = new HashMap<>();
        Group group = groupService.findByGroupName(banzu);
        for (int i = 0; i < ids.length; i++) {
            storageService.updatebanzu(ids[i], banzu, group.getId());
        }
        map.put("success", true);
        return map;
    }

    /***
     * 根据id修改重量
     * @param id
     * @param zhongliang
     * @return
     */
    @RequestMapping("/updatezhongliang")
    public String updatezhongliang(Integer id, Double zhongliang) {
        storageService.updatezhongliang(id, zhongliang);
        return "修改成功";
    }

    /***
     * 删除库存
     * @param id
     * @return
     */
    @RequestMapping("/deletekucun")
    public String deletekucun(Integer id) {
        Storage storage = storageService.findById(id);
        return storageService.deletekucun(id);
    }

    /***
     * 根据id，修改长度
     * @param changdu
     * @return
     */
    @RequestMapping("/updatechangdu")
    public String updatechangdu(Integer changdu, Integer id) {
        storageService.updatechangdu(changdu, id);
        return "修改成功";
    }

    /***
     * 根据id，修改厚度
     * @param houdu
     * @return
     */
    @RequestMapping("/updatehoudu")
    public String updatehoudu(String houdu, Integer[] idArr) {
        for (int i = 0; i < idArr.length; i++) {
            storageService.updatehoudu(houdu, idArr[i]);
        }
        return "修改成功";
    }

    //根据条件查询提货商品
    @RequestMapping("/selectTihuo")
    public Map<String,Object> selectTihuo(String pandianji) {
        System.out.println(pandianji);
        List<Storage> list = storageService.selectTihuo(pandianji);

        Double sumWeight = 0.00;
        for(Storage storage : list){
            sumWeight = sumWeight + storage.getRealityweight();
        }
        Map<String,Object> map = new HashMap<>();
        map.put("sumNum",list.size());
        map.put("sumWeight",sumWeight);
        map.put("rows",list);
        return map;
    }

    /***
     * 查询零售库存中是否存在符合条件的商品
     * @param storage
     * @return
     */
    @RequestMapping("/findLingShou")
    public List<Storage> findLingShou(Storage storage) {
        System.out.println(storage);
        List<Storage> storages = storageService.findLingShou(storage);
        for (Storage storage1 : storages) {
            storage1.setWeight(storage1.getRealityweight());
            storage1.setNum(1);
        }
        return storages;
    }

    /**
     * 统计查询
     */
    @RequestMapping("/tongji")
    public Map<String, Object> tongji(String stardate, String enddate, String clientname) throws ParseException {
        List<Map<String, Object>> list = null;
        if (StringUtil.isNotEmpty(clientname)){
            list = storageService.tongji(stardate + " 00:00:00", enddate + " 23:59:59", clientname);
        } else {
            list = storageService.tongji(stardate + " 00:00:00", enddate + " 23:59:59");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("rows", list);
        return map;
    }
}
