﻿using System;
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
        public Configuration() {
            InitializeComponent();
            this.SettingsToUi();
        }

        public void SettingsToUi() {
            string url = Settings.Store.url;
            string domain = Settings.Store.domain;
            string startApp = Settings.Store.startapp;

            this.textEgroupwareUrl.Text = url;
            this.textEgroupwareDomain.Text = domain;

            if (startApp == "1")
            {
                this.checkBoxStartApp.Checked = true;
            }
        }

        public void UiToSettings()
        {
            Settings.Store.url = this.textEgroupwareUrl.Text;
            Settings.Store.domain = this.textEgroupwareDomain.Text;

            if (this.checkBoxStartApp.Checked)
            {
                Settings.Store.startapp = "1";
            }
            else
            {
                Settings.Store.startapp = "0";
            }
        }

        private void button2_Click(object sender, EventArgs e) {
            this.UiToSettings();
            this.Close();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
