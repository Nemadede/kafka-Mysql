package connectclasses.apiClients;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.client.*;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcSunHttpTransport;
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;
import org.xml.sax.SAXException;


import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcSunHttpTransport;
import org.apache.xmlrpc.client.XmlRpcTransport;

public class MessageLoggingTransport  extends XmlRpcSunHttpTransport {

	public MessageLoggingTransport(XmlRpcClient pClient) {
		super(pClient);
		
	}

	private static final Logger log = Logger.getLogger(MessageLoggingTransport.class.getName());


    /**
     * Default constructor
     *
     * @see XmlRpcSunHttpTransport#XmlRpcSunHttpTransport(XmlRpcClient)
     * @param pClient client
     */

    /*
     * Dumps outgoing XML-RPC requests to the log
     */
//    @Override
//    protected void writeRequest(final XmlRpcStreamTransport.RequestWriter pWriter) throws  XmlRpcException
//    {
//        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
////        pWriter.write(baos);
//        log.info(baos.toString());
//        super.writeRequest(pWriter);
//    }


    /**
     * Dumps incoming XML-RPC responses to the log
     */
    @Override
    protected Object readResponse(XmlRpcStreamRequestConfig pConfig, InputStream pStream) throws XmlRpcException
    {
        final StringBuffer sb = new StringBuffer();

        try
        {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(pStream));
            String line = reader.readLine();
            while(line != null)
            {
                sb.append(line);
                line = reader.readLine();
            }
        }
        catch(final IOException e)
        {
            log.log(Level.SEVERE, "While reading server response", e);
        }

        log.info(sb.toString());

        final ByteArrayInputStream bais = new ByteArrayInputStream(sb.toString().getBytes());
        return super.readResponse(pConfig, bais);
    }
	

}
