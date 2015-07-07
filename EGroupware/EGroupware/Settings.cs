using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace pGina.Plugin.EGroupware
{
    class Settings {
        public enum HashEncoding { HEX = 0, BASE_64 = 1 };

        private static dynamic m_settings = new pGina.Shared.Settings.pGinaDynamicSettings(EGWWinLogin.PluginUuid);

        /**
         * 
         */
        public static dynamic Store {
            get { return m_settings; }
        }

        /**
         * 
         */
        static Settings() {
            m_settings.SetDefault("url", "https://localhost/egroupware/");
            m_settings.SetDefault("domain", "default");
            m_settings.SetDefault("startapp", "1");
            m_settings.SetDefault("jvmdb", "0");
            m_settings.SetDefault("jlogfile", "0");
        }
    }
}