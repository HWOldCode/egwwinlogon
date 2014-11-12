using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using pGina.Shared.Interfaces;
using pGina.Shared.Types;
using log4net;

using net.sf.jni4net;
using net.sf.jni4net.adaptors;

using java.lang;
using java.util;
using net.sf.jni4net.jni;
using Object = java.lang.Object;

/**
 * http://jni4net.googlecode.com/svn/tags/0.3.0.0/jni4net.n/src/Bridge.JVM.convertor.cs
 **/
namespace pGina.Plugin.EGroupware
{

    public class EGWWinLogin : IPluginAuthentication, IPluginAuthorization, IPluginAuthenticationGateway, IPluginConfiguration
    {

        private static readonly Guid m_uuid = new Guid("b094fee0-68c8-11e4-9803-0800200c9a66");

        private ILog _logger;
        protected JNIEnv env;
        protected Object _jEgwWinLogon;

        /**
         * 
         **/
        public EGWWinLogin() {
            this._logger = LogManager.GetLogger("pGina.Plugin.EGrroupware");

            try {
                this.initJava();
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }
        }

        /**
         * initJava
         **/
        private void initJava() {
            var setup = new BridgeSetup();

            setup.AddAllJarsClassPath(".");
            setup.Verbose = true;

            this.env = Bridge.CreateJVM(setup);

            if( this.env != null ) {
                try {
                    Class tmpClass = this.env.FindClass("egwwinlogon/EgwWinLogon");

                    if( tmpClass != null ) {
                        this._jEgwWinLogon = tmpClass.newInstance();
                    }
                }
                catch( System.Exception e ) {
                    this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
                }
            }
        }

        /**
         * _egwAuthenticateUser
         * 
         **/
        private bool _egwAuthenticateUser(string username, string password) {
            this._logger.InfoFormat("_egwAuthenticateUser {0}, {1}", username, password);

            if( this._jEgwWinLogon != null ) {

                int ret = this._jEgwWinLogon.getClass().Invoke<int>(
                    "egwAuthenticateUser",
                    "(Ljava/lang/String;Ljava/lang/String;)I",
                    username,
                    password
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

        public string Name
        {
            get
            {
                return "EGroupware Login";
            }
        }


        /**
         * @return string 
         **/
        public string Description
        {
            get { return "Authenticates EGroupware users."; }
        }


        /**
         * returns uuid of the plugin
         **/
        public Guid Uuid
        {
            get { return m_uuid; }
        }

        public string Version
        {
            get
            {
                return System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.ToString();
            }
        }

        public void Starting() {

        }

        public void Stopping() { }

        public BooleanResult AuthenticateUser(SessionProperties properties) {
            UserInformation userInfo = properties.GetTrackedSingle<UserInformation>();

            this.initJava();

            try {
                if( this._egwAuthenticateUser(userInfo.Username, userInfo.Password) ) {
                    this._logger.InfoFormat("Successfully authenticated {0}", userInfo.Username);

                    return new BooleanResult() { Success = true };
                }
            }
            catch( System.Exception e ) {
                this._logger.InfoFormat("Exception: {0} trace: {1}", e.Message, e.StackTrace);
            }


            string msg = "_jEgwWinLogon ist nicht gesetzt";

            if( this._jEgwWinLogon != null ) {
                msg = "_jEgwWinLogon ist gesetzt";
            }

            msg = msg + " cd:" + System.IO.Directory.GetCurrentDirectory();

            this._logger.ErrorFormat("Authentication failed for {0}", userInfo.Username);
            return new BooleanResult() { Success = false, Message = "Incorrect username or password. " + msg};
        }

        public Shared.Types.BooleanResult AuthenticatedUserGateway(Shared.Types.SessionProperties properties) {
            UserInformation userInfo = properties.GetTrackedSingle<UserInformation>();

            /*try {
                List<GroupGatewayRule> rules = GroupRuleLoader.GetGatewayRules();

            }*/

            // Always return success
            return new Shared.Types.BooleanResult { Success = true };
        }

        public BooleanResult AuthorizeUser(SessionProperties properties) {
            return new BooleanResult {
                Success = true,
                Message = string.Format("Allow")};
        }

        public void Configure() {
            //Configuration dialog = new Configuration();
            //dialog.ShowDialog();
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