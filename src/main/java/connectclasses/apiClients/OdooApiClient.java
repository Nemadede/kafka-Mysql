package connectclasses.apiClients;

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.xmlrpc.XmlRpcException;

import java.net.MalformedURLException;
import java.util.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class OdooApiClient {
	
	
	 private int uid = -1;
	    private String password;
	    private String database;

	    private URL host;
	    private static XmlRpcClient client = new XmlRpcClient();
	    private static XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
	    private static XmlRpcClientConfigImpl objectConfig = new XmlRpcClientConfigImpl();
	    

	    public static Log LOGGER = LogFactory.getLog(OdooApiClient.class);
	    
	    public static void setLogger(String logger) {
	        LOGGER = LogFactory.getLog(logger);
	    }
	    public static void setLogger (Log logger) {
	        LOGGER = logger;
	    }
	    
	    private static final int CONNECTION_TIMEOUT = 20000;
	    private static final int RECEIVE_TIMEOUT = 60000;
	    
	    public OdooApiClient() {
	    	  commonConfig.setConnectionTimeout(CONNECTION_TIMEOUT);
	          commonConfig.setReplyTimeout(RECEIVE_TIMEOUT);
	          commonConfig.setEnabledForExtensions(true);
	          commonConfig.setEnabledForExceptions(true);
	          objectConfig.setConnectionTimeout(CONNECTION_TIMEOUT);
	          objectConfig.setReplyTimeout(RECEIVE_TIMEOUT);
	          objectConfig.setEnabledForExtensions(true);
	          objectConfig.setEnabledForExceptions(true);
	    }
	    
	    
	    /**
	     * If called, xmlrpc request/response will be printed to stdout
	     */
	    public void dumpRequest () {

	        final XmlRpcTransportFactory transportFactory = new XmlRpcTransportFactory()
	        {
	            public XmlRpcTransport getTransport()
	            {

	                return new MessageLoggingTransport(client);
	            }
	        };
	        client.setTransportFactory(transportFactory);
	    }  
	 
	    
	    public int getUid() { return this.uid; }
	    public void setUid(int uid) { this.uid = uid; }
	    public boolean isConnected() { return this.uid != -1; }
	    public String getDatabase() {return this.database;}
	    public void setDatabase (String dbname) { this.database = dbname;}
	    public String getPassword() { return this.password; }
	    public void setPassword (String password) { this.password = password; }
	    public URL getHost() { return this.host; }

	    public void setHost(String host) {
	        try {
	            setHost(new URL(host));
	        } catch (MalformedURLException e) {
	            LOGGER.fatal(String.format("[OdooXmlRpc.setHost] %s", e.getMessage()));
	        };
	    }
	    public void setHost(URL newHost) { host = newHost; }
	    

	    
	    /**
	     * Initiate odoo session
	     * @param host the host
	     * @param database dbname
	     * @param user username
	     * @param password the password
	     * @return boolean bool
	     */
	    public boolean login (String host, String database, String user, String password) {
	        System.out.println("Starting login");
	        LOGGER.debug(String.format("Starting connection... to %s - %s as %s", host, database, user));
	        System.out.println("Starting login");
			//noinspection Since15
			if (host == null || host.isEmpty()) {
	            LOGGER.fatal("[OdooXmlRpc.login] Host should not be empty");
	            return false;
	        }

	        try {
	            this.host = new URL(host);
	            commonConfig.setServerURL(new URL(this.host, "/xmlrpc/2/common"));
	            objectConfig.setServerURL(new URL(this.host, "/xmlrpc/2/object"));

	            Object[] params = new Object[] {database, user, password, new Object[]{}};
	            Object res = client.execute(commonConfig, "authenticate", params);
	            this.password = password;
	            this.database = database;
	System.out.println("This shows that you might be a res client... awaitting test");
	            if (res instanceof Boolean ) this.uid = ((Boolean) res) ? 1 : 0;
	            else if (res instanceof Integer) this.uid = (Integer) res;
	            //else throw new Exception("bad response");
	        } catch (MalformedURLException urlException) {
	            LOGGER.fatal("[OdooXmlRpc.login] Malformed url.");
	            return false;
	        } catch (XmlRpcException e) {
	            LOGGER.error("[OdooXmlRpc.login] XmlRpcException. Details" + e.getMessage());
	        } catch (Exception e) {
	            LOGGER.error("[OdooXmlRpc.login] Exception. Details: " + e.getMessage());
	            return false;
	        }
	        System.out.println("you logged in succesfully");
	        return this.uid != -1;
	    }


	    /*
	     * Get details on Odoo version (debuging purpose)
	     * @return version map
	     */
	    @SuppressWarnings({ "unchecked"})
	    public Map<String,Object> getVersion () {
	        Map<String,Object> version = new HashMap();
	        try {
	            version = (HashMap<String,Object>) client.execute(commonConfig, "version", new Object[0]);
	        } catch (Exception e) {
	            LOGGER.error(e.getMessage());
	        }
	        return version;
	    }


	    /**
	     * Execute method with parameters
	     * @param modelName		string
	     * @param methodName	string
	     * @param methodParams 	array
	     * @return Object
	     */
	    public Object executeMethod (String modelName, String methodName, Object[] methodParams) {
	        Object res = new Object();

	        try {
	            Object[] params = new Object[]{
	                    this.database,
	                    this.uid,
	                    this.password,
	                    modelName,
	                    methodName,
	                    methodParams
	            };
	            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	            
//	            String json = ow.writeValueAsString(params);
//	            System.out.println(json);
	            
	            res = client.execute(objectConfig, "execute_kw", params);

	        } catch (Exception e) {
	            LOGGER.error("[OdooXmlRpc.executeMethod] Exception during execution of " + modelName + "=>" + methodName + ". Details: " + e.getMessage());
	        }
	        return res;
	    }


	    /**
	     * Execute method with parameters
	     * @param modelName		string
	     * @param methodName	string
	     * @param args 	list
	     * @param kw 	map
	     * @return Object
	     */
	    public Object executeMethod (String modelName, String methodName, List args, Map kw) {
	        Object res = new Object();

	        try {
	            Object[] params = new Object[]{
	                    this.database,
	                    this.uid,
	                    this.password,
	                    modelName,
	                    methodName,
	                    args,
	                    kw
	            };
	            res = client.execute(objectConfig, "execute_kw", params);

	        } catch (Exception e) {
	            LOGGER.error("[OdooXmlRpc.executeMethod] Exception during execution of " + modelName + "=>" + methodName + ". Details: " + e.getMessage());
	        }
	        return res;
	    }
	    /**
	     * List field, type, help of an Odoo model
	     *
	     * @param modelName	string
	     * @return listRecords	map
	     */
	    @SuppressWarnings({ "unchecked", "rawtypes" })
	    public Map listRecords (String modelName) {
	        Map result = new HashMap();
	        try {

	            client.setConfig(objectConfig);
	            result = (Map<String, Map<String, Object>>) client.execute("execute_kw", asList(
	            		  this.database, this.uid, this.password,
	                    modelName, "fields_get",
	                    emptyList(),
	                    new HashMap() {{
	                        put("attributes", asList("string", "help", "type"));
	                    }}
	            ));

	        } catch (Exception e) {
	            LOGGER.error("[OdooXmlRpc.listRecords] Exception when listing Records of " + modelName + ". Details: " + e.getMessage());
	        }
	        return result;
	    }

	    /**
	     * Create record, return its id on success, -1 on failure
	     * @param modelName	string	model name
	     * @param data	map		a HashMap of model data
	     * @return	id	integer
	     */
	    public Integer createRecord (String modelName, Map data) {
	        Integer recordId = -1;
	        try {
	            client.setConfig(objectConfig);
	            recordId = (Integer) client.execute("execute_kw", asList(
	                    this.database, this.uid, this.password,
	                    modelName, "create",
	                    asList(
	                            data
	                    )
	            ));

	        } catch (Exception e) {
	            LOGGER.error("[OdooXmlRpc.createRecord] Exception when creating record in " + modelName + ". Details: " + e.getMessage());
	        }
	        return recordId;
	    }

	    /**
	     * Update record
	     * @param modelName	string	the model name
	     * @param data	map		the record data (without id)
	     * @param id	integer	the id of the existing record
	     */
	    public void updateRecord (String modelName, Map<String,Object> data, int id) {
	        try {
	            client.setConfig(objectConfig);
	            client.execute("execute_kw", asList(
	                    this.database, this.uid, this.password,
	                    modelName, "write",
	                    asList(
	                            asList(id),
	                            data
	                    )
	            ));

	        } catch (Exception e) {
	            LOGGER.error("[OdooXmlRpc.updateRecord] Exception when updating record no" + String.valueOf(id) + " in " + modelName + ". Details: " + e.getMessage());
	        }
	    }

	    @SuppressWarnings({ "unchecked", "rawtypes" })
	    public Map getRecordById (String modelName, int id, List fields) {
	        Map<String,Object> record = new HashMap();
	        try {
	            List filters = new ArrayList();
	            filters.add(Arrays.asList("id", "=", String.valueOf(id)));
	            List records = getRecords(modelName, fields, filters);
	            if (records.size() == 1) {
	                record = (Map<String, Object>) records.get(0);
	            }
	        } catch (Exception e) {
	            LOGGER.error("[OdooXmlRpc.getRecordById] Exception when getting record no" + String.valueOf(id) + " in " + modelName + ". Details: " + e.getMessage());
	        }
	        return record;
	    }

	    @SuppressWarnings({"rawtypes" })
	    public List getRecords (String model, List fields) {
	        return getRecords (model, fields, null);
	    }
	    public List getRecords (String model, List fields, List<List> filters) {
	        List result = new ArrayList();
	        if (fields == null) fields = Arrays.asList("id", "name");

	        List AllFilters = new ArrayList();
	        AllFilters.add(asList("id", ">", "0"));

	        if (filters != null) {
	            for (List filter : filters){
	                AllFilters.add(filter);
	            }
	        }

	        try {
	            client.setConfig(objectConfig);
				final List finalFields = fields;
	            result = asList((Object[]) client.execute("execute_kw", asList(
	                    this.database, this.uid, this.password,
	                    model, "search_read",
	                    asList(
	                            AllFilters
	                    ),
	                    new HashMap() {{
	                        put("fields", finalFields);
	                        put("context", new HashMap(){{put("active_test", false);}});
	                    }}


	            )));

	        } catch (Exception e) {
	            LOGGER.error(String.format("[OdooXmlRpc.getRecords] Exception. Details: %s", e.getMessage()));
	        }
	        return result;
	    }

	    /**
	     * Return a ready to import List for a many2many field
	     * Doc: https://www.odoo.com/documentation/11.0/reference/orm.html#odoo.models.Model.write
	     * @param fieldValue List Integer List of ids
	     * @return List
	     */
	    public static List<List<Object>> many2manyOverride (List<Integer> fieldValue) {
	        return Arrays.asList(Arrays.asList(
	                6,
	                0,
	                Arrays.asList(
	                        fieldValue == null ? new Integer[0] : fieldValue.toArray()
	                )
	        ));
	    }
	    
	    
	    
	    
	    
	    
	    
	    

	    
}
