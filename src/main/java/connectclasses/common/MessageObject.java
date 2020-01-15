package connectclasses.common;

//import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.json.JSONObject;



public class MessageObject {

    private Float amount_tax;
    private Float amount_total;
    private Float amount_untaxed;
    private String invoice_status;
    private Integer id;
    private Integer company_id;
    public MessageObject(){}
    public MessageObject(Float amount_tax, Float amount_total, Float amount_untaxed, String invoice_status, Integer id, Integer company_id) {
        this.amount_tax = amount_tax;
        this.amount_total = amount_total;
        this.amount_untaxed = amount_untaxed;
        this.invoice_status = invoice_status;
        this.id = id;
        this.company_id = company_id;
    }

    public float getAmount_tax() {
        return amount_tax;
    }

    public void setAmount_tax(float amount_tax) {
        this.amount_tax = amount_tax;
    }

    public float getAmount_total() {
        return amount_total;
    }

    public void setAmount_total(float amount_total) {
        this.amount_total = amount_total;
    }

    public float getAmount_untaxed() {
        return amount_untaxed;
    }

    public void setAmount_untaxed(float amount_untaxed) {
        this.amount_untaxed = amount_untaxed;
    }

    public String getInvoice_status() {
        return invoice_status;
    }

    public void setInvoice_status(String invoice_status) {
        this.invoice_status = invoice_status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }

    public static String SCHEMA_KEY = "TableKey";
    public static String SCHEMA_VALUE ="schema_name";
    public static String AMOUNT_TAX = "amount_tax";
    public static String AMOUNT_TOTAL = "amount_total";
    public static String AMOUNT_UNTAXED = "amount_untaxed";
    public static String INVOICE_STATUS = "invoice_status";
    public static String SALE_ORDER_ID ="id";
    public static String COMPANY_ID = "company_id";


    public static Schema KEY_SCHEMA = SchemaBuilder.struct()
            .name(SCHEMA_KEY).version(1)
           .field("tableName",Schema.STRING_SCHEMA)
            .build();
    public static Schema TABLE_SCHEMA = SchemaBuilder.struct()
            .name(SCHEMA_VALUE).version(1)
            .field( AMOUNT_TAX, Schema.FLOAT32_SCHEMA)
            .field( AMOUNT_TOTAL, Schema.FLOAT32_SCHEMA)
            .field( AMOUNT_UNTAXED , Schema.FLOAT32_SCHEMA)
            .field(INVOICE_STATUS,Schema.STRING_SCHEMA)
            .field(SALE_ORDER_ID,Schema.INT32_SCHEMA)
            .field(COMPANY_ID,Schema.INT32_SCHEMA)
            .build();


    public static MessageObject fromJson(JSONObject jsonObject){
        MessageObject messageObject = new MessageObject();
        messageObject.setAmount_tax(jsonObject.getFloat(AMOUNT_TAX));
        messageObject.setAmount_total(jsonObject.getFloat(AMOUNT_TOTAL));
        messageObject.setAmount_untaxed(jsonObject.getFloat(AMOUNT_UNTAXED));
        messageObject.setInvoice_status(jsonObject.getString(INVOICE_STATUS));
        messageObject.setId(jsonObject.getInt(SALE_ORDER_ID));
        messageObject.setCompany_id(jsonObject.getInt(COMPANY_ID));
        return messageObject;
    }
}
