namespace EGroupware {
    partial class Configuration
    {
        /// <summary>
        /// Erforderliche Designervariable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Verwendete Ressourcen bereinigen.
        /// </summary>
        /// <param name="disposing">True, wenn verwaltete Ressourcen gelöscht werden sollen; andernfalls False.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null)) {
                components.Dispose();
            }

            base.Dispose(disposing);
        }

        #region Vom Windows Form-Designer generierter Code

        /// <summary>
        /// Erforderliche Methode für die Designerunterstützung.
        /// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Configuration));
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.checkBoxJavaLogFile = new System.Windows.Forms.CheckBox();
            this.checkBoxJVMDB = new System.Windows.Forms.CheckBox();
            this.textEgroupwareDomain = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.textEgroupwareUrl = new System.Windows.Forms.TextBox();
            this.labelEgroupwareUrl = new System.Windows.Forms.Label();
            this.button1 = new System.Windows.Forms.Button();
            this.button2 = new System.Windows.Forms.Button();
            this.label2 = new System.Windows.Forms.Label();
            this.numericUpDownJVMPort = new System.Windows.Forms.NumericUpDown();
            this.groupBox1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownJVMPort)).BeginInit();
            this.SuspendLayout();
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.numericUpDownJVMPort);
            this.groupBox1.Controls.Add(this.label2);
            this.groupBox1.Controls.Add(this.checkBoxJavaLogFile);
            this.groupBox1.Controls.Add(this.checkBoxJVMDB);
            this.groupBox1.Controls.Add(this.textEgroupwareDomain);
            this.groupBox1.Controls.Add(this.label1);
            this.groupBox1.Controls.Add(this.textEgroupwareUrl);
            this.groupBox1.Controls.Add(this.labelEgroupwareUrl);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(360, 174);
            this.groupBox1.TabIndex = 2;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Egroupware";
            // 
            // checkBoxJavaLogFile
            // 
            this.checkBoxJavaLogFile.AutoSize = true;
            this.checkBoxJavaLogFile.Location = new System.Drawing.Point(138, 92);
            this.checkBoxJavaLogFile.Name = "checkBoxJavaLogFile";
            this.checkBoxJavaLogFile.Size = new System.Drawing.Size(111, 17);
            this.checkBoxJavaLogFile.TabIndex = 7;
            this.checkBoxJavaLogFile.Text = "Write Java Logfile";
            this.checkBoxJavaLogFile.UseVisualStyleBackColor = true;
            // 
            // checkBoxJVMDB
            // 
            this.checkBoxJVMDB.AutoSize = true;
            this.checkBoxJVMDB.Location = new System.Drawing.Point(14, 92);
            this.checkBoxJVMDB.Name = "checkBoxJVMDB";
            this.checkBoxJVMDB.Size = new System.Drawing.Size(118, 17);
            this.checkBoxJVMDB.TabIndex = 6;
            this.checkBoxJVMDB.Text = "JVM Debug Enable";
            this.checkBoxJVMDB.UseVisualStyleBackColor = true;
            // 
            // textEgroupwareDomain
            // 
            this.textEgroupwareDomain.Location = new System.Drawing.Point(95, 60);
            this.textEgroupwareDomain.Name = "textEgroupwareDomain";
            this.textEgroupwareDomain.Size = new System.Drawing.Size(259, 20);
            this.textEgroupwareDomain.TabIndex = 4;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(9, 63);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(46, 13);
            this.label1.TabIndex = 3;
            this.label1.Text = "Domain:";
            // 
            // textEgroupwareUrl
            // 
            this.textEgroupwareUrl.Location = new System.Drawing.Point(95, 26);
            this.textEgroupwareUrl.Name = "textEgroupwareUrl";
            this.textEgroupwareUrl.Size = new System.Drawing.Size(259, 20);
            this.textEgroupwareUrl.TabIndex = 2;
            // 
            // labelEgroupwareUrl
            // 
            this.labelEgroupwareUrl.AutoSize = true;
            this.labelEgroupwareUrl.Location = new System.Drawing.Point(6, 29);
            this.labelEgroupwareUrl.Name = "labelEgroupwareUrl";
            this.labelEgroupwareUrl.Size = new System.Drawing.Size(83, 13);
            this.labelEgroupwareUrl.TabIndex = 1;
            this.labelEgroupwareUrl.Text = "Egroupware Url:";
            // 
            // button1
            // 
            this.button1.Location = new System.Drawing.Point(297, 192);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(75, 23);
            this.button1.TabIndex = 3;
            this.button1.Text = "Close";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // button2
            // 
            this.button2.Location = new System.Drawing.Point(216, 192);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(75, 23);
            this.button2.TabIndex = 4;
            this.button2.Text = "Save";
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.button2_Click);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(11, 123);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(128, 13);
            this.label2.TabIndex = 8;
            this.label2.Text = "Remote JVM Debug Port:";
            // 
            // numericUpDownJVMPort
            // 
            this.numericUpDownJVMPort.Location = new System.Drawing.Point(145, 121);
            this.numericUpDownJVMPort.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.numericUpDownJVMPort.Name = "numericUpDownJVMPort";
            this.numericUpDownJVMPort.Size = new System.Drawing.Size(104, 20);
            this.numericUpDownJVMPort.TabIndex = 10;
            this.numericUpDownJVMPort.Value = new decimal(new int[] {
            8889,
            0,
            0,
            0});
            // 
            // Configuration
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(384, 227);
            this.Controls.Add(this.button2);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.groupBox1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "Configuration";
            this.Text = "Egroupware Settings";
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownJVMPort)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.TextBox textEgroupwareUrl;
        private System.Windows.Forms.Label labelEgroupwareUrl;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.Button button2;
        private System.Windows.Forms.TextBox textEgroupwareDomain;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.CheckBox checkBoxJVMDB;
        private System.Windows.Forms.CheckBox checkBoxJavaLogFile;
        private System.Windows.Forms.NumericUpDown numericUpDownJVMPort;
        private System.Windows.Forms.Label label2;
    }
}