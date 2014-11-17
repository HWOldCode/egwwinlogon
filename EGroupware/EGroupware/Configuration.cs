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
        public Configuration() {
            InitializeComponent();
            this.SettingsToUi();
        }

        public void SettingsToUi() {
            string url = Settings.Store.url;
            string domain = Settings.Store.domain;

            this.textEgroupwareUrl.Text = url;
            this.textEgroupwareDomain.Text = domain;
        }

        public void UiToSettings()
        {
            Settings.Store.url = this.textEgroupwareUrl.Text;
            Settings.Store.domain = this.textEgroupwareDomain.Text;
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
