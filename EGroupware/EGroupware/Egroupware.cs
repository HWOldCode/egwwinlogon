using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;

using log4net;

using pGina.CredentialProvider.Registration;
using pGina.Shared.Interfaces;
using pGina.Shared.Types;
using pGina.Shared.Settings;

using net.sf.jni4net;
using net.sf.jni4net.adaptors;

using java.lang;
using java.util;
using net.sf.jni4net.inj;
using net.sf.jni4net.jni;
using net.sf.jni4net.utils;
using Object = java.lang.Object;
using EGroupware;
using Abstractions.WindowsApi;
using System.IO;
using System.Diagnostics;
using System.Threading;
using java.io;
using System.Net;

namespace pGina.Plugin.EGroupware {

    /**
     * Class EGWWinLogin
     */
    public class EGWWinLogin : IPluginAuthentication, IPluginAuthorization, IPluginAuthenticationGateway, IPluginConfiguration, IPluginEventNotifications {
        public static readonly Guid PluginUuid = new Guid("b094fee0-68c8-11e4-9803-0800200c9a66");

        private static EGWWinLogin _self;
        private static ILog _logger;

        protected Object _jEgwWinLogonUpdater;
        protected Object _jEgwWinLogon;

        protected bool _isService = false;

        /**
         * EGWWinLogin
         */
        public EGWWinLogin() {
            // set vars
            EGWWinLogin._self   = this;
            EGWWinLogin._logger = LogManager.GetLogger("pGina.Plugin.EGroupware");

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
            curPath = curPath.Replace("Plugins", "");

            return curPath;
        }

        /**
         * getDLLHash
         */
        private string getDLLHash() {
            FileStream fc = System.IO.File.OpenRead(this.getAppDir() + "Plugins\\" + 
                Assembly.GetExecutingAssembly().GetName().Name + ".dll");

            System.Security.Cryptography.MD5 md5 = new System.Security.Cryptography.MD5CryptoServiceProvider();
            byte[] md5hash = md5.ComputeHash(fc);

            fc.Close();

            string hash = BitConverter.ToString(md5hash).Replace("-", "").ToLower();

            return hash;
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

        // ------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------

        /**
         * _registerJavaMethods
         */
        private void _registerJavaMethods(JNIEnv env) {
            try {
                Class egroupwareDllClass = env.FindClass("egwwinlogon/service/EgroupwareDLL");

                if( egroupwareDllClass != null ) {
                    var methods = new List<JNINativeMethod>();

                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "isRunAsService", "_isRunAsService", "()Z"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "isJavaLoggingFile", "_isJavaLoggingFile", "()Z"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "getAppDir", "_getAppDir", "()Ljava/lang/String;"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "startProcessInSession", "_startProcessInSession", "(ILjava/lang/String;)I"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "startUserProcessInSession", "_startUserProcessInSession", "(ILjava/lang/String;)I"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "startProcessInWinsta0Default", "_startProcessInWinsta0Default", "(Ljava/lang/String;)I"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "startProcessInWinsta0Winlogon", "_startProcessInWinsta0Winlogon", "(Ljava/lang/String;)I"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "logInfo", "_logInfo", "(Ljava/lang/String;)V"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "logError", "_logError", "(Ljava/lang/String;)V"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "validateCredentials", "_validateCredentials", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "getCredentials", "_getCredentials", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "setSetting", "_setSetting", "(Ljava/lang/String;Ljava/lang/String;)V"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "getSetting", "_getSetting", "(Ljava/lang/String;)Ljava/lang/String;"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "logoffSession", "_logoffSession", "(I)Z"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "getUsername", "_getUsername", "(I)Ljava/lang/String;"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "getDLLHash", "_getDLLHash", "()Ljava/lang/String;"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "getSysFingerprint", "_getSysFingerprint", "()Ljava/lang/String;"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "getSystemStr", "_getSystemStr", "()Ljava/lang/String;"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "getMachineName", "_getMachineName", "()Ljava/lang/String;"));
                    methods.Add(JNINativeMethod.Create(typeof(EGWWinLogin), "setDeviceEnabled", "_setDeviceEnabled", "(Ljava/lang/String;Ljava/lang/String;Z)V"));

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
         * _isJavaLoggingFile
         * method to java
         */
        private static bool _isJavaLoggingFile(IntPtr @__envp, JniLocalHandle @__obj) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                string jlogfile = Settings.Store.jlogfile;

                if( jlogfile == "1" ) {
                    return true;
                }
            }
            catch (global::System.Exception __ex)
            {
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
        private static int _startProcessInSession(IntPtr @__envp, JniLocalHandle @__obj, int sessionId, JniLocalHandle cmdLine) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                string tcmdLine = Convertor.StrongJ2CString(env, cmdLine);
                int tsessionid = sessionId;

                if( tsessionid != 0 ) {
                    Process proc = pInvokes.StartProcessInSession(tsessionid, tcmdLine);

                    if( proc != null ) {
                        return (int)proc.Id;
                    }
                }
            }
            catch( global::System.Exception __ex ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(int);
        }

        /**
         * _startUserProcessInSession
         * method to java
         */
        private static int _startUserProcessInSession(IntPtr @__envp, JniLocalHandle @__obj, int sessionId, JniLocalHandle cmdLine) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                string tcmdLine = Convertor.StrongJ2CString(env, cmdLine);
                int tsessionid = sessionId;

                Process proc = pInvokesEgw.StartUserProcessInSessionEgw(tsessionid, tcmdLine);

                if( proc != null ) {
                    //EGWWinLogin._logger.InfoFormat("_startUserProcessInSession: {0} ", (int)proc.Id);
                    return (int)proc.Id;
                }

                return 0;
            }
            catch( global::System.Exception __ex ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(int);
        }

        /**
         * _startProcessInWinsta0Default
         * method to java
         */
        private static int _startProcessInWinsta0Default(IntPtr @__envp, JniLocalHandle @__obj, JniLocalHandle cmdLine) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                string tcmdLine = Convertor.StrongJ2CString(env, cmdLine);

                ApplicationLoader.PROCESS_INFORMATION procInfo;

                if (ApplicationLoader.StartProcessAndBypassUAC(tcmdLine, 0, out procInfo)) {
                    return (int)procInfo.dwProcessId;
                }
            }
            catch (global::System.Exception __ex) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(int);
        }

        /**
         * _startProcessInWinsta0Winlogon
         * method to java
         */
        private static int _startProcessInWinsta0Winlogon(IntPtr @__envp, JniLocalHandle @__obj, JniLocalHandle cmdLine) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                string tcmdLine = Convertor.StrongJ2CString(env, cmdLine);

                ApplicationLoader.PROCESS_INFORMATION procInfo;

                if (ApplicationLoader.StartProcessAndBypassUAC(tcmdLine, 1, out procInfo)) {
                    return (int)procInfo.dwProcessId;
                }
            }
            catch (global::System.Exception __ex) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(int);
        }

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
        private static bool _validateCredentials(IntPtr @__envp, JniLocalHandle @__obj, 
            JniLocalHandle username, JniLocalHandle domain, JniLocalHandle password) 
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
         * _getCredentials
         * method to java
         */
        private static JniLocalHandle _getCredentials(IntPtr @__envp, JniLocalHandle @__obj,
            JniLocalHandle title, JniLocalHandle message)
        {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                string treturn  = "";
                string ttitle   = Convertor.StrongJ2CString(env, title);
                string tmessage = Convertor.StrongJ2CString(env, message);

                NetworkCredential nc = pInvokes.GetCredentials(ttitle, tmessage);

                if (nc != null) {
                    treturn = treturn + "{username:" + nc.UserName + "}";
                    treturn = treturn + "{domain:" + nc.Domain + "}";
                    treturn = treturn + "{password:" + nc.Password + "}";
                }

                return Convertor.StrongC2JString(env, treturn);
            }
            catch (global::System.Exception __ex) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(JniLocalHandle);
        }

        /**
         * _setSetting
         * method to java
         */
        private static void _setSetting(IntPtr @__envp, JniLocalHandle @__obj, JniLocalHandle name, JniLocalHandle value) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                string tname    = Convertor.StrongJ2CString(env, name);
                string tvalue   = Convertor.StrongJ2CString(env, value);

                Settings.Store.SetDefault(tname, tvalue);
            }
            catch (global::System.Exception __ex) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }
        }

        /**
         * _getSetting
         * method to java
         */
        private static JniLocalHandle _getSetting(IntPtr @__envp, JniLocalHandle @__obj, JniLocalHandle name) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                string tname = Convertor.StrongJ2CString(env, name);
                string tvalue = Settings.Store.GetSetting(tname);

                return Convertor.StrongC2JString(env, tvalue);
            }
            catch (global::System.Exception __ex)
            {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(JniLocalHandle);
        }

        /**
         * _logoffSession
         * method to java
         */
        private static bool _logoffSession(IntPtr @__envp, JniLocalHandle @__obj, int sessionId) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                return pInvokes.LogoffSession(sessionId);
            }
            catch (global::System.Exception __ex) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return false;
        }

        /**
         * _getUsername
         * method to java
         */
        private static JniLocalHandle _getUsername(IntPtr @__envp, JniLocalHandle @__obj, int sessionId) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                return Convertor.StrongC2JString(env, pInvokes.GetUserName(sessionId));
            }
            catch (global::System.Exception __ex) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(JniLocalHandle);
        }

        /**
         * _getDLLHash
         * method to java
         */
        private static JniLocalHandle _getDLLHash(IntPtr @__envp, JniLocalHandle @__obj) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                return Convertor.StrongC2JString(env, EGWWinLogin._self.getDLLHash());
            }
            catch( global::System.Exception __ex ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(JniLocalHandle);
        }

        /**
         * _getSysFingerprint
         * method to java
         */
        private static JniLocalHandle _getSysFingerprint(IntPtr @__envp, JniLocalHandle @__obj) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                return Convertor.StrongC2JString(env, SysFingerPrint.Value());
            }
            catch( global::System.Exception __ex ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(JniLocalHandle);
        }

        /**
         * _getSystemStr
         * method to java
         */
        private static JniLocalHandle _getSystemStr(IntPtr @__envp, JniLocalHandle @__obj) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                return Convertor.StrongC2JString(env, SysFingerPrint.getSystemStr());
            }
            catch( global::System.Exception __ex ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(JniLocalHandle);
        }

        /**
         * _getMachineName
         * method to java
         */
        private static JniLocalHandle _getMachineName(IntPtr @__envp, JniLocalHandle @__obj) {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                return Convertor.StrongC2JString(env, System.Environment.MachineName);
            }
            catch (global::System.Exception __ex) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }

            return default(JniLocalHandle);
        }

        /**
         * _setDeviceEnabled
         * method to java
         */
        private static void _setDeviceEnabled(IntPtr @__envp, JniLocalHandle @__obj, 
            JniLocalHandle deviceGuid, JniLocalHandle instancePath, bool enable)
        {
            JNIEnv env = JNIEnv.Wrap(@__envp);

            try {
                string tdeviceGuid = Convertor.StrongJ2CString(env, deviceGuid);
                string tinstancePath = Convertor.StrongJ2CString(env, instancePath);

                DeviceHelper.SetDeviceEnabled(
                    new Guid(tdeviceGuid), 
                    tinstancePath, 
                    enable
                    );
            }
            catch( global::System.Exception __ex ) {
                EGWWinLogin._logger.InfoFormat("Exception: {0} trace: {1}", __ex.Message, __ex.StackTrace);
                env.ThrowExisting(__ex);
            }
        }

        // ------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------

        /**
         * initEgwWinLogonJava
         */
        private void initEgwWinLogonJava() {
            string jvmdb        = Settings.Store.jvmdb;
            string jvmdbport    = Settings.Store.jvmdbport;

            var setup = new BridgeSetup();

            if( jvmdb == "1" ) {
                setup.AddJVMOption("-Xrunjdwp:transport=dt_socket,server=y,address=" + jvmdbport + ",suspend=n");
            }

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
            try {
                if( this._jEgwWinLogon != null ) {
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
         * _egwAuthenticatedUserGateway
         */
        private bool _egwAuthenticatedUserGateway(string username, string password, string domain) {
            EGWWinLogin._logger.InfoFormat("_egwAuthenticatedUserGateway {0}", username);

            if( domain == null ) {
                domain = "";
            }

            if( this._jEgwWinLogon != null ) {
                bool ret = this._jEgwWinLogon.Invoke<bool>(
                    "egwAuthenticatedUserGateway",
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z",
                    username,
                    password,
                    domain
                    );

                EGWWinLogin._logger.InfoFormat("return: {0}", ret);

                if (ret == true ) {
                    return true;
                }
            }
            else {
                EGWWinLogin._logger.InfoFormat("_jEgwWinLogon is empty in _egwAuthenticatedUserGateway");
            }

            return false;
        }

        /**
         * _egwAuthorizeUser 
         */
        private bool _egwAuthorizeUser(string username, string password, string domain) {
            EGWWinLogin._logger.InfoFormat("_egwAuthorizeUser {0}", username);

            if( domain == null ) {
                domain = "";
            }

            if( this._jEgwWinLogon != null ) {
                bool ret = this._jEgwWinLogon.Invoke<bool>(
                    "egwAuthorizeUser",
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z",
                    username,
                    password,
                    domain
                    );

                EGWWinLogin._logger.InfoFormat("return: {0}", ret);

                if( ret == true ) {
                    return true;
                }
            }
            else {
                EGWWinLogin._logger.InfoFormat("_jEgwWinLogon is empty in _egwAuthorizeUser");
            }

            return false;
        }

        /**
         * _egwIsLogin
         */
        private bool _egwIsLogin(string username) {
            if( this._jEgwWinLogon != null ) {
                int ret = this._jEgwWinLogon.Invoke<int>(
                    "isEgwLogin",
                    "(Ljava/lang/String;)I",
                    username
                    );

                if( ret == 1 ) {
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

        // ------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------

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
            try {
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

            EGWWinLogin._logger.InfoFormat("AuthenticatedUserGateway: username {0} domain: {1}",
                username, domain);

            if (this._egwAuthenticatedUserGateway(username, password, domain)) {
                return new Shared.Types.BooleanResult { Success = true };
            }

            return new Shared.Types.BooleanResult { Success = false };
        }

        /**
         * AuthorizeUser 
         */
        public BooleanResult AuthorizeUser(SessionProperties properties) {
            UserInformation userInfo = properties.GetTrackedSingle<UserInformation>();

            string username = userInfo.Username;
            string password = userInfo.Password;
            string domain = userInfo.Domain;

            EGWWinLogin._logger.InfoFormat("AuthorizeUser: username {0} domain: {1}",
                username, domain);

            if (this._egwAuthorizeUser(username, password, domain)) {
                return new BooleanResult {
                    Success = true,
                    Message = string.Format("Allow")
                };
            }

            return new BooleanResult {
                Success = false,
                Message = string.Format("no Authorize")
            };
        }

        /**
         * SessionChange
         */
        public void SessionChange(int SessionId, System.ServiceProcess.SessionChangeReason Reason, SessionProperties properties) {
            if( properties != null ) {
                int sessionid = SessionId;

                UserInformation userInfo = properties.GetTrackedSingle<UserInformation>();
                
                string startApp = Settings.Store.startapp;
                string username = userInfo.Username;

                EGWWinLogin._logger.InfoFormat("SessionChange: {0}, {1}", Reason, username);

                switch( Reason ) {
                    case System.ServiceProcess.SessionChangeReason.SessionLogon:
                        this._egwSessionChange(5, username, sessionid);
                        break;

                    case System.ServiceProcess.SessionChangeReason.SessionLogoff:
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
         * Configure
         */
        public void Configure() {
            Configuration dialog = new Configuration();
            dialog.ShowDialog();
        }
    }
}