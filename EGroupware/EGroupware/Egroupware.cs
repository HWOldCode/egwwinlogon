using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;

using pGina.Shared.Interfaces;
using pGina.Shared.Types;
using log4net;

using net.sf.jni4net;
using net.sf.jni4net.adaptors;

using java.lang;
using java.util;
using net.sf.jni4net.inj;
using net.sf.jni4net.jni;
using net.sf.jni4net.utils;
using Object = java.lang.Object;
using EGroupware;
using pGina.CredentialProvider.Registration;
using Abstractions.WindowsApi;
using System.IO;
using System.Diagnostics;
using System.Threading;

/**
 * http://jni4net.googlecode.com/svn/tags/0.3.0.0/jni4net.n/src/Bridge.JVM.convertor.cs
 **/
namespace pGina.Plugin.EGroupware
{
    /**
     * Class EGWWinLogin
     */
    public class EGWWinLogin : IPluginAuthentication, IPluginAuthorization, IPluginAuthenticationGateway, IPluginConfiguration, IPluginEventNotifications
    {

        public static readonly Guid PluginUuid = new Guid("b094fee0-68c8-11e4-9803-0800200c9a66");

        private ILog _logger;
        protected JNIEnv env;
        protected Object _jEgwWinLogon;

        Dictionary<string, Process> _plist;
        protected bool _shouldStop = false;

        /**
         * constructor
         */
        public EGWWinLogin() {
            this._logger = LogManager.GetLogger("pGina.Plugin.EGrroupware");

            this._plist = new Dictionary<string, Process>();

            try {
                this.initJava();
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }
        }

        /**
         * getAppDir
         */
        private string getAppDir() {
            string curPath = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
            curPath = curPath.Replace("Plugins\\Core", "");

            return curPath;
        }

        private string getJavaInstallationPath() {
            string javaKey = "SOFTWARE\\JavaSoft\\Java Runtime Environment\\";

            using( Microsoft.Win32.RegistryKey rk = Microsoft.Win32.Registry.LocalMachine.OpenSubKey(javaKey) ) {
                string currentVersion = rk.GetValue("CurrentVersion").ToString();
                
                using( Microsoft.Win32.RegistryKey key = rk.OpenSubKey(currentVersion) ) {
                    return key.GetValue("JavaHome").ToString();
                }
            }
        }

        /**
         * initJava
         */
        private void initJava() {
            var setup = new BridgeSetup();
            //string curPath = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
            //this._logger.InfoFormat("cP: " + curPath);
            //curPath = curPath.Replace("Plugins\\Core", "");
            //this._logger.InfoFormat("cP: " + curPath);

            setup.AddAllJarsClassPath(this.getAppDir() + ".");
            setup.Verbose = true;

            this.env = Bridge.CreateJVM(setup);

            if( this.env != null ) {
                try {
                    Class tmpClass = this.env.FindClass("egwwinlogon/service/EgwWinLogon");

                    if( tmpClass != null ) {
                        this._jEgwWinLogon = tmpClass.newInstance();
                        this._initEgroupware();
                    }
                }
                catch( System.Exception e ) {
                    this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
                }
            }
        }

        /**
         * _initEgroupware
         */
        private void _initEgroupware() {
            try{
                if( this._jEgwWinLogon != null ) {
                    string url = Settings.Store.url;
                    string domain = Settings.Store.domain;
                    string machinename = System.Environment.MachineName;

                    this._jEgwWinLogon.Invoke(
                        "initEgroupware",
                        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                        url, domain, machinename);
                }
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }
        }

        /**
         * _egwSessionChange
         */
        private void _egwSessionChange(int change) {
            try{
                if( this._jEgwWinLogon != null ) {
                    this._jEgwWinLogon.Invoke(
                        "egwSessionChange",
                        "(I)V", 
                        change);
                }
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }
        }

        /**
         * registerJava
         */
        private void registerJava() {
            if( Bridge.Setup.BindNative ) {
                global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> methods = new global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod>();
                global::System.Type @__type = typeof(EGWWinLogin);
                //methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getDirectoryMap", "getDirectoryMap2", "()Ljava/util/Map;"));

                //MethodInfo initializer = Registry.GetWrapperInitializer(typeof(EGWWinLogin), "__Init2");
                //RegistryRecord record = Registry.GetCLRRecord(typeof(Bridge));
                //Registry.RegisterNative(initializer, env, record.JVMProxy, record.JVMInterface);
            }
        }

        /**
         * _egwAuthenticateUser
         * 
         */
        private bool _egwAuthenticateUser(string username, string password) {
            this._logger.InfoFormat("_egwAuthenticateUser {0}, {1}", username, password);

            if( this._jEgwWinLogon != null ) {

                int ret = this._jEgwWinLogon.Invoke<int>(
                    "egwAuthenticateUser",
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I",
                    username,
                    password,
                    SysFingerPrint.Value()
                    );

                this._logger.InfoFormat("return: {0}", ret);

                if( ret == 1 ) {
                    return true;
                }
            }
            else {
                this._logger.InfoFormat("_jEgwWinLogon is empty in _egwAuthenticateUser");
            }

            return false;
        }

        /**
         * _egwGetDescription
         * 
         */
        private string _egwGetDescription() {
            try{
                if( this._jEgwWinLogon != null ) {
                    object ret = this._jEgwWinLogon.Invoke<object>(
                        "egwGetDescription",
                        "()Ljava/lang/String;");

                    return ret.ToString();
                }
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }

            return "Authenticates EGroupware users.";
        }

        /**
         * _egwGetName
         */
        private string _egwGetName() {
            try{
                if( this._jEgwWinLogon != null ) {
                    object ret = this._jEgwWinLogon.Invoke<object>(
                        "egwGetName",
                        "()Ljava/lang/String;");

                    return ret.ToString();
                }
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }

            return "EGroupware Login";
        }

        /**
         * _egwGetVersion
         */
        private string _egwGetVersion() {
            try{
                if( this._jEgwWinLogon != null ) {
                    object ret = this._jEgwWinLogon.Invoke<object>(
                        "egwGetVersion",
                        "()Ljava/lang/String;");

                    return ret.ToString();
                }
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }

            return System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.ToString();
        }

        /**
         * _egwIsError
         */
        private bool _egwIsError() {
            try{
                if( this._jEgwWinLogon != null ) {
                    int ret = this._jEgwWinLogon.Invoke<int>(
                        "egwIsError",
                        "()I");

                    if( ret == 1 ) {
                        return true;
                    }
                }
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }

            return false;
        }

        /**
         * _egwGetError
         */
        private string _egwGetError() {
            try{
                if( this._jEgwWinLogon != null ) {
                    object ret = this._jEgwWinLogon.Invoke<object>(
                        "egwGetError",
                        "()Ljava/lang/String;");

                    return ret.ToString();
                }

                return "_jEgwWinLogon is Empty!";
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);

                return e.Message;
            }
        }

        /**
         * Name
         */
        public string Name {
            get {
                return this._egwGetName();
            }
        }


        /**
         * Description
         * 
         * @return string 
         */
        public string Description {
            get { return this._egwGetDescription(); }
        }


        /**
         * returns uuid of the plugin
         **/
        public Guid Uuid {
            get { return PluginUuid; }
        }

        /**
         * Version
         */
        public string Version {
            get {
                return this._egwGetVersion();
            }
        }

        /**
         * Starting
         */
        public void Starting() {
            this._shouldStop = false;

            Thread thread = new Thread(new ThreadStart(this.workThreadFunction));
            thread.Start();

            try{
                if( this._jEgwWinLogon != null ) {
                    this._jEgwWinLogon.Invoke(
                        "egwStarting",
                        "()V");
                }
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }
        }

        /**
         * Stopping
         */
        public void Stopping() {
            this._shouldStop = true;

            try{
                if( this._jEgwWinLogon != null ) {
                    this._jEgwWinLogon.Invoke(
                        "egwStopping",
                        "()V");
                }
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }
        }

        /**
         * AuthenticateUser
         */
        public BooleanResult AuthenticateUser(SessionProperties properties) {
            UserInformation userInfo = properties.GetTrackedSingle<UserInformation>();

            string msg = "";

            this.initJava();

            try {
                if( this._egwAuthenticateUser(userInfo.Username, userInfo.Password) ) {
                    this._logger.InfoFormat("Successfully authenticated {0}", userInfo.Username);

                    return new BooleanResult() { 
                        Success = true
                    };
                }
                else {
                    if( this._egwIsError() ) {
                        msg = this._egwGetError();
                    }
                    else {
                        msg = "Incorrect username or password.";
                    }
                }
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);

                msg = e.Message;
            }

            //msg = msg + " cd:" + System.IO.Directory.GetCurrentDirectory();

            //this._logger.ErrorFormat("Authentication failed for {0}", userInfo.Username);
            return new BooleanResult() { 
                Success = false, 
                Message = msg
            };
        }

        /**
         * AuthenticatedUserGateway
         */
        public Shared.Types.BooleanResult AuthenticatedUserGateway(Shared.Types.SessionProperties properties) {
            UserInformation userInfo = properties.GetTrackedSingle<UserInformation>();

            /*try {
                List<GroupGatewayRule> rules = GroupRuleLoader.GetGatewayRules();

            }*/

            // Always return success
            return new Shared.Types.BooleanResult { Success = true };
        }

        /**
         * AuthorizeUser
         */
        public BooleanResult AuthorizeUser(SessionProperties properties) {
            return new BooleanResult {
                Success = true,
                Message = string.Format("Allow")};
        }

        protected void startUserApp(string username) {
            string applicationName = "\"" + this.getJavaInstallationPath() + 
                "\\bin\\java.exe\" -jar \"" + this.getAppDir() + "\\egwwinlogon.jar\" " + username;

            this._logger.InfoFormat(applicationName);

            ApplicationLoader.PROCESS_INFORMATION procInfo;

            if( ApplicationLoader.StartProcessAndBypassUAC(applicationName, out procInfo) ) {
                if( this._plist.ContainsKey(username) ) {
                    this._plist.Remove(username);
                }

                this._plist.Add(username, Process.GetProcessById((int)procInfo.dwProcessId));
            }
        }

        /**
         * SessionChange
         */
        public void SessionChange(System.ServiceProcess.SessionChangeDescription changeDescription, SessionProperties properties) {
            if( properties != null ) {
                
                UserInformation userInfo = properties.GetTrackedSingle<UserInformation>();

                this._logger.InfoFormat("SessionChange: {0}, {1}", changeDescription.Reason, userInfo.Username);

                switch( changeDescription.Reason ) {
                    case System.ServiceProcess.SessionChangeReason.SessionLogon:
                        this.startUserApp(userInfo.Username);

                        this._egwSessionChange(5);
                        //LogonEvent(changeDescription.SessionId);
                        break;

                    case System.ServiceProcess.SessionChangeReason.SessionLogoff:
                        
                        if( this._plist.ContainsKey(userInfo.Username) ) {
                            Process tp = this._plist[userInfo.Username];

                            if( tp != null ) {
                                if( !tp.HasExited ) {
                                    tp.Close();
                                }
                            }

                            this._plist.Remove(userInfo.Username);
                        }

                        this._egwSessionChange(6);
                        break;
                }

                

                if( this._egwIsError() ) {
                    this._logger.InfoFormat("Egroupware Error: {0}", this._egwGetError());
                }
            }
        }

        public void workThreadFunction() {
            while( !this._shouldStop ) {
                Thread.Sleep(100);

                List<string> list = new List<string>(this._plist.Keys);

                foreach( string k in list ) {
                    Process tp = this._plist[k];

                    if( tp != null ) {
                        if( tp.HasExited ) {
                            this.startUserApp(k);
                            this._logger.InfoFormat("App is closed: {0}", k);
                        }
                    }
                }
            }
        }

        /**
         * Configure
         */
        public void Configure() {
            Configuration dialog = new Configuration();
            dialog.ShowDialog();
        }

        /**
         * Main Example and Debug used
         **/
        public static void Main()
        {
            /*EGWWinLogin egw = new EGWWinLogin();

            egw.initJava();*/
        }
    }
}