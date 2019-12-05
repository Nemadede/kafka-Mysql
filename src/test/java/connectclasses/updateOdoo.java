package connectclasses;

import connectclasses.apiClients.OdooApiClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class updateOdoo {
OdooApiClient odooApiClient;
    private Map<String,String> data = new HashMap<>();

    public Map dataPut(){
         Map<String,String> data = new HashMap<>();
         data.put("id","46");
         data.put("name","SO009");
        data.put("state","draft");
        data.put("date_order","2019-08-19 09:51:00");
        data.put("require_signature","true");
        data.put("create_date","2019-09-19 09:50:58.515172");
        data.put("user_id","6");
        data.put("partner_id","11");
        data.put("partner_invoice_id","31");
        data.put("partner_shipping_id","31");
        data.put("pricelist_id","1");
        data.put("invoice_status","no");
        data.put("note"," ");
        data.put("amount_untaxed","73880");
        data.put("amount_tax","0.00");
        data.put("amount_total","73880");
        data.put("currency_rate","1.000000");
        data.put("payment_term_id","1");
        data.put("company_id","1");
        data.put("team_id","3");
        data.put("create_uid","1");
        data.put("write_uid","1");
        data.put("write_date","2019-09-25 15:02:04.652108");
        data.put("picking_policy","direct");
        data.put("warehouse_id","1");
         return data;
    }

    @Test
    public void createRecord(){

        odooApiClient = new OdooApiClient();
        odooApiClient.login("http://localhost:8069","demodb","demodb@demo.com","123456");

        odooApiClient.createRecord("sale.order",dataPut());
    }

}
