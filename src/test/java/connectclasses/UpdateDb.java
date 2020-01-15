package connectclasses;

import connectclasses.apiClients.OdooApiClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UpdateDb {
    Map map = new HashMap();
    OdooApiClient odooApiClient = new OdooApiClient();
    String host = "http://localhost:8069"; //user's odoo configurations
    String db ="demodb";
    String user = "demodb@demo.com";
    String password = "123456";

    @Test
    public void Update(){
        odooApiClient.login(host,db,user,password);
        Random rand = new Random();
        int amt = 60000 + rand.nextInt(1000);
        for(int i= 0; i<5; i++)
        {

            map.put("name","SO030");
            map.put("state","draft");
            map.put("date_order","2019-09-19 09:51:00");
            map.put("require_signature",true);
            map.put("require_payment",true);
            map.put("create_date","2019-09-19 09:50:58");
            map.put("user_id",rand.nextInt(7));
            map.put("partner_id",rand.nextInt(12));
            map.put("partner_invoice_id",rand.nextInt(31));
            map.put("partner_shipping_id",rand.nextInt(25));
            map.put("pricelist_id",1);
            map.put("invoice_status","no");
            map.put("note"," ");
            map.put("amount_tax",0);
            map.put("amount_untaxed",amt);
            map.put("amount_total",amt);
            map.put("currency_rate",1.0000000);
            map.put("payment_term_id",1);
            map.put("company_id",1);
            map.put("team_id",rand.nextInt(4));
            map.put("create_uid",1);
            map.put("write_uid",1);
            map.put("write_date","2019-09-25 15:02:04:6512108");
            map.put("cart_recovery_email_sent",false);
            map.put("picking_policy","direct");
            map.put("warehouse_id",1);
            odooApiClient.createRecord("sale.order",map);
        }
    }
}
