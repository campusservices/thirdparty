package uwi.thirdparty.service;

import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
//Imports for changing password
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.stereotype.Service;

//******************************************************************************
//**  ActiveDirectory
//*****************************************************************************/
/**
 *   Provides static methods to authenticate users, change passwords, etc. 
 *
 ******************************************************************************/
@Service
public class ActiveDirectory {

   
   @SuppressWarnings("unused")
private static LdapContext context;
//    private static final InitialLdapContext context;
    private ActiveDirectory(){ }




  //**************************************************************************
  //** getConnection
  //*************************************************************************/
  /** Used to authenticate a user given a username/password and domain name.
   *  Provides an option to identify a specific a Active Directory server.
   */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean getConnection(String username, String password, String domainName, String serverName) throws NamingException {
        boolean authenticated = false;
        if (domainName==null){
            try{
                String fqdn = java.net.InetAddress.getLocalHost().getCanonicalHostName();
                if (fqdn.split("\\.").length>1) domainName = fqdn.substring(fqdn.indexOf(".")+1);
            }
            catch(java.net.UnknownHostException e){}
        }
        
        //System.out.println("Authenticating " + username + "@" + domainName + " through " + serverName);

        if (password!=null){
            password = password.trim();
            if (password.length()==0) password = null;
        }

        //bind by using the specified username/password
        Hashtable props = new Hashtable();
        String principalName = username + "@" + domainName;
        props.put(Context.SECURITY_PRINCIPAL, principalName);
        if (password!=null) props.put(Context.SECURITY_CREDENTIALS, password);

        String ldapURL ="LDAP://cavehillsrv1.cavehill.uwi.edu:389";
//        String ldapURL = "ldap://" + ((serverName==null)? domainName : serverName + "." + domainName) + '/';
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, ldapURL);
        try{
        	context =  new InitialLdapContext(props, null);
        	authenticated = true;
        }
        catch(javax.naming.CommunicationException e){
            throw new NamingException("Failed to connect to " + domainName + ((serverName==null)? "" : " through " + serverName));
        }
        catch(NamingException e){
            throw new NamingException("Failed to authenticate " + username + "@" + domainName + ((serverName==null)? "" : " through " + serverName));
        }
        return authenticated;
    }
  
}
