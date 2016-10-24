using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

using pGina.Shared.Interfaces;
using pGina.Shared.Types;
using pGina.Plugin.EGroupware;

namespace EGroupware
{
    public partial class Configuration : Form
    {
        /**
         * 
         */
        public Configuration() {
            InitializeComponent();
            this.SettingsToUi();
        }

        /**
         * SettingsToUi
         */
        public void SettingsToUi() {
            string url = Settings.Store.url;
            string domain = Settings.Store.domain;
            string jvmdb = Settings.Store.jvmdb;
            string jlogfile = Settings.Store.jlogfile;
            string jvmparam = Settings.Store.jvmparam;

            this.textEgroupwareUrl.Text = url;
            this.textEgroupwareDomain.Text = domain;

            if( jvmdb == "1" ) {
                this.checkBoxJVMDB.Checked = true;
            }

            if( jlogfile == "1" ) {
                this.checkBoxJavaLogFile.Checked = true;
            }

            this.textJVMParameter.Text = jvmparam;
        }

        /**
         * UiToSettings
         */
        public void UiToSettings() {
            Settings.Store.url = this.textEgroupwareUrl.Text;
            Settings.Store.domain = this.textEgroupwareDomain.Text;

            if( this.checkBoxJVMDB.Checked ) {
                Settings.Store.jvmdb = "1";
            }
            else {
                Settings.Store.jvmdb = "0";
            }

            if( this.checkBoxJavaLogFile.Checked ) {
                Settings.Store.jlogfile = "1";
            }
            else {
                Settings.Store.jlogfile = "0";
            }

            Settings.Store.jvmparam = this.textJVMParameter.Text;
        }

        /**
         * button2_Click
         */
        private void button2_Click(object sender, EventArgs e) {
            this.UiToSettings();
            this.Close();
        }

        /**
         * button1_Click
         */
        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
