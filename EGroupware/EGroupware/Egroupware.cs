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
using java.io;

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

        private static EGWWinLogin _self;
        private static ILog _logger;

        protected Object _jEgwWinLogonUpdater;
        protected Object _jEgwWinLogon;

        protected Dictionary<string, Process> _plist;
        protected bool _shouldStop = false;

        protected bool _isService = false;

        /**
         * constructor
         */
        public EGWWinLogin() {
            // set vars
            EGWWinLogin._self   = this;
            EGWWinLogin._logger = LogManager.GetLogger("pGina.Plugin.EGrroupware");

            this._plist = new Dictionary<string, Process>();

            // -------------------------------
            // check who is run this plugin
            string currentProcessFileName = System.Diagnostics.Process.GetCurrentProcess().MainModule.FileName;

            if( currentProcessFileName.IndexOf("pGina.Configuration.exe") != -1) {
                this._isService = false;
            }
            else {
                this._isService = true;
            }

            // -------------------------------
            // init java updater app

            /*try
            {
                this.initUpdaterJava();
            }
            catch (System.Exception e)
            {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }*/

            // -------------------------------
            // init egwwinlogon java app
            try {
                this.initEgwWinLogonJava();
            }
            catch( System.Exception e ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
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

        /**
         * getJavaInstallationPath
         */
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
         * _registerJavaMethods
         */
        private void _registerJavaMethods(JNIEnv env) {
            try {
                Class egroupwareDllClass = env.FindClass("egwwinlogon/service/EgroupwareDLL");

                if( egroupwareDllClass != null ) {
                    var methods = new List<JNINativeMethod>();

                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "isRunAsService", "_isRunAsService", "()Z"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "getAppDir", "_getAppDir", "()Ljava/lang/String;"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "startProcessInSession", "_startProcessInSession", "(ILjava/lang/String;)I"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "logInfo", "_logInfo", "(Ljava/lang/String;)V"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "logError", "_logError", "(Ljava/lang/String;)V"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "validateCredentials", "_validateCredentials", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "setSetting", "_setSetting", "(Ljava/lang/String;Ljava/lang/String;)V"));

                    JNINativeMethod.Register(methods, egroupwareDllClass, env);
                }
                else {
                    EGWWinLogin._logger.InfoFormat("EgroupwareDLL not found.");
                }
            }
            catch (System.Exception e) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }
        }

        /**
         * _isRunAsService
         * method to java
         */
        private static bool _isRunAsService(IntPtr @__envp, JniLocalHandle @__obj) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                return EGWWinLogin._self._isService;
            }
            catch( global::System.Exception __ex ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return false;
        }

        /**
         * _getAppDir
         * method to java
         */
        private static JniLocalHandle _getAppDir(IntPtr @__envp, JniLocalHandle @__obj) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                return Convertor.StrongC2JString(env, EGWWinLogin._self.getAppDir());
            }
            catch( global::System.Exception __ex ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(JniLocalHandle);
        }

        /**
         * _startProcessInSession
         * method to java
         */
        private static int _startProcessInSession(IntPtr @__envp, JniLocalHandle @__obj, int sessionId, JniLocalHandle cmdLine)
        {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                string tcmdLine = Convertor.StrongJ2CString(env, cmdLine);
                int tsessionid = sessionId;

                Process proc = pInvokes.StartUserProcessInSession(tsessionid, tcmdLine);

                if( proc != null ) {
                    return (int) proc.Id;
                }
            }
            catch (global::System.Exception __ex)
            {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(int);
        }

        /*private static void _mapDiveInSession()
        {
            pInvokes.StartUserProcessInSession
        }*/

        /**
         * _logInfo
         * method to java
         */
        private static void _logInfo(IntPtr @__envp, JniLocalHandle @__obj, JniLocalHandle msg) {
            JNIEnv env = JNIEnv.Wrap(@__envp);
            
            try {
                string tmsg = Convertor.StrongJ2CString(env, msg);

                EGWWinLogin._logger.InfoFormat("_logInfo: {0}", tmsg);
            }
            catch( global::System.Exception __ex ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }
        }

        /**
         * _logError
         * method to java
         */
        private static void _logError(IntPtr @__envp, JniLocalHandle @__obj, JniLocalHandle msg) {
            JNIEnv env = JNIEnv.Wrap(@__envp);
            
            try {
                string tmsg = Convertor.StrongJ2CString(env, msg);

                EGWWinLogin._logger.ErrorFormat("_logError: {0}", tmsg);
            }
            catch( global::System.Exception __ex ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }
        }

        /**
         * _validateCredentials
         * method to java
         */
        private static bool _validateCredentials(IntPtr @__envp, JniLocalHandle @__obj, JniLocalHandle username, JniLocalHandle domain, JniLocalHandle password)
        {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                string tusername    = Convertor.StrongJ2CString(env, username);
                string tdomain      = Convertor.StrongJ2CString(env, domain);
                string tpassword    = Convertor.StrongJ2CString(env, password);

                return pInvokes.ValidateCredentials(tusername, tdomain, tpassword);
            }
            catch (global::System.Exception __ex) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return false;
        }

        /**
         * _setSetting
         * method to java
         */
        private static void _setSetting(IntPtr @__envp, JniLocalHandle @__obj, JniLocalHandle name, JniLocalHandle value)
        {
            JNIEnv env = JNIEnv.Wrap(@__envp);
            //string tname    = name;
            //string tvalue   = value;

            //Settings.Store.SetDefault(tname, tvalue);
        }

        /**
         * initUpdaterJava
         */
        private void initUpdaterJava() {
            var setup = new BridgeSetup();
            setup.AddAllJarsClassPath(this.getAppDir() + ".");
            setup.Verbose = true;

            JNIEnv env = Bridge.CreateJVM(setup);

            // register natives methods
            Class tmpClass = env.FindClass("egwwinlogon/updater/WinLogonUpdater");

            if( tmpClass != null ) {
                this._registerJavaMethods(env);
                this._jEgwWinLogonUpdater = tmpClass.newInstance();
            }
        }

        /**
         * initEgwWinLogonJava
         */
        private void initEgwWinLogonJava() {
            var setup = new BridgeSetup();
            
            setup.AddAllJarsClassPath(this.getAppDir() + ".");
            setup.Verbose = true;

            JNIEnv env = Bridge.CreateJVM(setup);

            if( env != null ) {
                try {
                    this._registerJavaMethods(env);

                    Class tmpClass = env.FindClass("egwwinlogon/service/EgwWinLogon");

                    if( tmpClass != null ) {
                        this._jEgwWinLogon = tmpClass.newInstance();
                        this._initEgroupware();
                    }
                }
                catch( System.Exception e ) {
                    EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
                }
            }
        }

        /**
         * _initEgroupware
         */
        private void _initEgroupware() {
            try{
                if( this._jEgwWinLogon != null ) {

                    // set base settings
                    string url          = Settings.Store.url;
                    string domain       = Settings.Store.domain;
                    string mname        = System.Environment.MachineName;
                    string fingerprint  = SysFingerPrint.Value();

                    this._egwSetSetting("url", url);
                    this._egwSetSetting("domain", domain);
                    this._egwSetSetting("machinename", mname);
                    this._egwSetSetting("sysfingerprint", fingerprint);

                    if( !this._isService ) {
                        // no conflict set other port for 
                        this._egwSetSetting("httpserverport", "8107");
                    }
   
                    this._jEgwWinLogon.Invoke(
                        "initEgroupware",
                        "()V");
                }
            }
            catch( System.Exception e ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }
        }

        /**
         * _egwSessionChange
         */
        private void _egwSessionChange(int change, string username, int sessionid) {
            try{
                string changeStr = change.ToString();

                if( this._jEgwWinLogon != null ) {
                    this._jEgwWinLogon.Invoke(
                        "egwSessionChange",
                        "(Ljava/lang/String;Ljava/lang/String;I)V",
                        changeStr,
                        username,
                        sessionid
                        );
                }
            }
            catch( System.Exception e ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }
        }

        /**
         * _egwAuthenticateUser
         * 
         */
        private bool _egwAuthenticateUser(string username, string password, string domain, int sessionid) {
            EGWWinLogin._logger.InfoFormat("_egwAuthenticateUser {0}", username);

            if( domain == null ) {
                domain = "";
            }

            if( this._jEgwWinLogon != null ) {
                int ret = this._jEgwWinLogon.Invoke<int>(
                    "egwAuthenticateUser",
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I",
                    username,
                    password,
                    domain,
                    sessionid
                    );

                EGWWinLogin._logger.InfoFormat("return: {0}", ret);

                if( ret == 1 ) {
                    return true;
                }
            }
            else {
                EGWWinLogin._logger.InfoFormat("_jEgwWinLogon is empty in _egwAuthenticateUser");
            }

            return false;
        }

        /**
         * _egwIsLogin
         */
        private bool _egwIsLogin(string username) {
            //this._logger.InfoFormat("_egwIsLogin {0}", username);

            if (this._jEgwWinLogon != null) {
                int ret = this._jEgwWinLogon.Invoke<int>(
                    "isEgwLogin",
                    "(Ljava/lang/String;)I",
                    username
                    );

                //this._logger.InfoFormat("return: {0}", ret);

                if (ret == 1) {
                    return true;
                }
            }
            else {
                EGWWinLogin._logger.InfoFormat("_jEgwWinLogon is empty in _egwIsLogin");
            }

            return false;
        }

        /**
         * _egwSetSetting
         */
        private void _egwSetSetting(string name, string value) {
            EGWWinLogin._logger.InfoFormat("_egwSetSetting {0} {1}", name, value);

            if (this._jEgwWinLogon != null) {
                try {
                    this._jEgwWinLogon.Invoke(
                        "setSetting",
                        "(Ljava/lang/String;Ljava/lang/String;)V",
                        name,
                        value
                        );
                }
                catch (System.Exception e) {
                    EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
                    EGWWinLogin._logger.InfoFormat("Exception: {0}: {1}", name, value);
                }
            }
            else {
                EGWWinLogin._logger.InfoFormat("_jEgwWinLogon is empty in _egwSetSetting");
            }
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
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
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
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
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
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
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
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
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
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);

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
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
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
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }
        }

        /**
         * AuthenticateUser
         */
        public BooleanResult AuthenticateUser(SessionProperties properties) {
            int sessionid = 0;

            UserInformation userInfo = properties.GetTrackedSingle<UserInformation>();
            
            string username = userInfo.Username;
            string password = userInfo.Password;
            string domain   = userInfo.Domain;

            string msg = "";

            //this.initJava();

            try {
                if( this._egwAuthenticateUser(username, password, domain, sessionid) ) {
                    EGWWinLogin._logger.InfoFormat("Successfully authenticated {0}", userInfo.Username);

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
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);

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

            string username = userInfo.Username;
            string password = userInfo.Password;
            string domain   = userInfo.Domain;

            EGWWinLogin._logger.InfoFormat("AuthenticatedUserGateway: username {0} password: {1} domain: {2}", 
                username, password, domain);
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
            /*this._logger.InfoFormat("AuthorizeUser: username {0} password: {1} domain: {2}",
                username, password, domain);*/

            /*try
            {*/
                bool alwaysAuth = true;

                EGWWinLogin._logger.DebugFormat("AuthenticateUser({0})", properties.Id.ToString());

                UserInformation userInfo = properties.GetTrackedSingle<UserInformation>();

                string username = userInfo.Username;
                string password = userInfo.Password;
                string domain = userInfo.Domain;

                EGWWinLogin._logger.DebugFormat("Found username: {0}", username);

                if( alwaysAuth /* && */)
                {

                }

                EGWWinLogin._logger.ErrorFormat("Failed to authenticate user: {0}", username);

                // Note that we don't include a message.  We are a last chance auth, and want previous/failed plugins
                //  to have the honor of explaining why.
                //return new BooleanResult() { Success = false, Message = null }; 
            /*}
            catch (Exception e)
            {
                EGWWinLogin._logger.ErrorFormat("AuthenticateUser exception: {0}", e);
                throw;  // Allow pGina service to catch and handle exception
            }*/

            return new BooleanResult {
                Success = true,
                Message = string.Format("Allow")};
        }

        /**
         * startUserApp
         */
        protected void startUserApp(int sessionId, string username) {
            string applicationName = "\"" + this.getJavaInstallationPath() + 
                "\\bin\\javaw.exe\" -jar \"" + this.getAppDir() + "\\egwwinlogon.jar\" " + username;

            EGWWinLogin._logger.InfoFormat(applicationName);

            /*ApplicationLoader.PROCESS_INFORMATION procInfo;

            if( ApplicationLoader.StartProcessAndBypassUAC(applicationName, out procInfo) ) {
                if( this._plist.ContainsKey(username) ) {
                    this._plist.Remove(username);
                }

                this._plist.Add(username, Process.GetProcessById((int)procInfo.dwProcessId));
            }*/

            Process proc = pInvokes.StartProcessInSession(sessionId, applicationName);

            if( proc != null ) {
                if (this._plist.ContainsKey(username))
                {
                    this._plist.Remove(username);
                }

                this._plist.Add(username, proc);
            }
        }

        /**
         * SessionChange
         */
        public void SessionChange(System.ServiceProcess.SessionChangeDescription changeDescription, SessionProperties properties) {
            if( properties != null ) {

                int sessionid = changeDescription.SessionId;

                UserInformation userInfo = properties.GetTrackedSingle<UserInformation>();
                
                string startApp = Settings.Store.startapp;
                string username = userInfo.Username;

                EGWWinLogin._logger.InfoFormat("SessionChange: {0}, {1}", changeDescription.Reason, username);

                switch( changeDescription.Reason ) {
                    case System.ServiceProcess.SessionChangeReason.SessionLogon:

                        if( startApp == "1" ) {
                            this.startUserApp(sessionid, username);
                        }
                        else {
                            this._plist.Add(username, null);
                        }

                        this._egwSessionChange(5, username, sessionid);
                        //LogonEvent(changeDescription.SessionId);
                        break;

                    case System.ServiceProcess.SessionChangeReason.SessionLogoff:
                        
                        if( this._plist.ContainsKey(username) ) {
                            Process tp = this._plist[username];

                            if( tp != null ) {
                                if( !tp.HasExited ) {
                                    tp.Close();
                                }
                            }

                            this._plist.Remove(username);
                        }

                        this._egwSessionChange(6, username, sessionid);
                        break;

                    case System.ServiceProcess.SessionChangeReason.SessionLock:
                        this._egwSessionChange(7, username, sessionid);
                        break;

                    case System.ServiceProcess.SessionChangeReason.SessionUnlock:
                        this._egwSessionChange(8, username, sessionid);
                        break;
                }

                

                if( this._egwIsError() ) {
                    EGWWinLogin._logger.InfoFormat("Egroupware Error: {0}", this._egwGetError());
                }
            }
        }

        /**
         * workThreadFunction 
         */
        public void workThreadFunction() {
            while( !this._shouldStop ) {
                Thread.Sleep(100);

                List<string> list = new List<string>(this._plist.Keys);

                foreach( string k in list ) {
                    Process tp = this._plist[k];

                    if( tp != null ) {
                        string startApp = Settings.Store.startapp;

                        if( startApp == "1" ) {
                            // is app activ
                            if( tp.HasExited ) {
                                //this.startUserApp(k);
                                EGWWinLogin._logger.InfoFormat("App is closed: {0}", k);
                            }
                        }
                    }

                    // is logged in
                    if( !this._egwIsLogin(k) ) {
                        // logout user
                        WTS.closeLocalUserSession(k);
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
        public static void Main() {
            //WTS.ListUsers("localhost");
            //usbControl u = new usbControl();

            /*while( true ) {
                Thread.Sleep(1000);
            }*/
            /*EGWWinLogin egw = new EGWWinLogin();

            while( true ) {
                Thread.Sleep(1000);
            }*/
        }
    }
}