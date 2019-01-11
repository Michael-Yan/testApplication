package com.zzz.monitor.bean;

import java.util.List;

/**
 * Created by YDX on 2018/12/6 0006.
 */
public class DeviceListBean {

    /**
     * total : 4
     * Authorization : Bearer Tu7GAB9nfKwj7G1dpdtD2lj8qIaWgJeC
     * objects : [{"sn":"92510647878472","is_online":true,"op":"中国电信","sim_one_network_type":"4G","sim_two_network_type":"2G","sim_one_signal_level":3,"sim_two_signal_level":3,"address":"在万家福购物商场(花都区芙蓉粮食管理所东北)附近","latitude":"23.474548","longitude":"113.230464","power_type":"out","snapshot":"https://snapshots.reiniot.com/92510647878472/1544061996.webp","online_at":"2018-12-06 10:06:36","expire_at":{"date":"2019-05-26 15:43:40.000000","timezone_type":3,"timezone":"Asia/Shanghai"},"name":"山清水秀F9-1-2505唐先生-"},{"sn":"92510801226133","is_online":true,"op":"中国电信","sim_one_network_type":"4G","sim_two_network_type":"2G","sim_one_signal_level":1,"sim_two_signal_level":2,"address":"在丰盛路小区附近","latitude":"23.475112","longitude":"113.233931","power_type":"out","snapshot":"https://snapshots.reiniot.com/92510801226133/1544061422.webp","online_at":"2018-12-06 10:06:59","expire_at":{"date":"2019-05-26 15:43:40.000000","timezone_type":3,"timezone":"Asia/Shanghai"},"name":"山清水秀F7-1-201红姐-"},{"sn":"92518409248005","is_online":true,"op":"中国电信","sim_one_network_type":"4G","sim_two_network_type":"2G","sim_one_signal_level":1,"sim_two_signal_level":3,"address":"在万家福购物商场(花都区芙蓉粮食管理所东北)附近","latitude":"23.474548","longitude":"113.230464","power_type":"out","snapshot":"https://snapshots.reiniot.com/92518409248005/1544062096.webp","online_at":"2018-12-06 10:08:16","expire_at":{"date":"2019-05-26 15:43:40.000000","timezone_type":3,"timezone":"Asia/Shanghai"},"name":"山清水秀F6-2-1202黄先生-"}]
     */

    private int total;
    private String Authorization;
    private List<Camera> objects;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getAuthorization() {
        return Authorization;
    }

    public void setAuthorization(String Authorization) {
        this.Authorization = Authorization;
    }

    public List<Camera> getObjects() {
        return objects;
    }

    public void setObjects(List<Camera> objects) {
        this.objects = objects;
    }
}
