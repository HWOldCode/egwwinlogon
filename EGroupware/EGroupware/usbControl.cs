using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Management;

namespace EGroupware {
    class usbControl: IDisposable {

        // used for monitoring plugging and unplugging of USB devices.
        private ManagementEventWatcher _watcherAttach;
        private ManagementEventWatcher _watcherRemove;

        public usbControl() {
            string sql = "SELECT * FROM __InstanceCreationEvent WITHIN 2 WHERE TargetInstance ISA 'Win32_PnPEntity'";

            // Add USB plugged event watching
            _watcherAttach = new ManagementEventWatcher();
            //var queryAttach = new WqlEventQuery("SELECT * FROM Win32_DeviceChangeEvent WHERE EventType = 2");
            _watcherAttach.EventArrived += new EventArrivedEventHandler(watcher_EventArrived);
            _watcherAttach.Query = new WqlEventQuery(/*"SELECT * FROM Win32_DeviceChangeEvent WHERE EventType = 1"*/ sql);
            _watcherAttach.Start();

            // Add USB unplugged event watching
            _watcherRemove = new ManagementEventWatcher();
            //var queryRemove = new WqlEventQuery("SELECT * FROM Win32_DeviceChangeEvent WHERE EventType = 3");
            _watcherRemove.EventArrived += new EventArrivedEventHandler(watcher_EventRemoved);
            _watcherRemove.Query = new WqlEventQuery("SELECT * FROM Win32_DeviceChangeEvent WHERE EventType = 3");
            _watcherRemove.Start();
        }

        void watcher_EventArrived(object sender, EventArrivedEventArgs e) {

            /*foreach( PropertyData pd in e.NewEvent.Properties ) {
                if( pd != null ) {
                    if( pd.Value != null ) {
                        if( pd.Value.GetType() == typeof(System.String) ) {
                            Console.WriteLine("\t" + pd.Name + ":" + pd.Value + "\t" + pd.Value.GetType());
                        }
                        else if( pd.Value.GetType() == typeof(System.UInt64) ) {
                            Console.WriteLine("\t" + pd.Name + ":" + pd.Value.ToString() + "\t" + pd.Value.GetType());
                            
                        }
                        else {
                            Console.WriteLine("\t" + pd.Name + ":" + pd.Value.GetType());
                        }
                    }
                }
            }

            Console.WriteLine("test");*/

            ManagementBaseObject instance = (ManagementBaseObject )e.NewEvent["TargetInstance"];

            Guid t = new Guid((string)instance["ClassGuid"]);
            Console.WriteLine(t.ToString());

            foreach (var property in instance.Properties) {
                Console.WriteLine(property.Name + " = " + property.Value);
            }

            DeviceHelper.SetDeviceEnabled(t, (string)instance["DeviceId"], false);
        }

        void watcher_EventRemoved(object sender, EventArrivedEventArgs e) {
            /*foreach (PropertyData pd in e.NewEvent.Properties)
            {
                Console.WriteLine("\t" + pd.Name + ":" + pd.Value + "\t" + pd.Value.GetType());
            }*/
            Console.WriteLine("test1");
        }

        public void Dispose() {
            _watcherAttach.Stop();
            _watcherRemove.Stop();
            //Thread.Sleep(1000);
            _watcherAttach.Dispose();
            _watcherRemove.Dispose();
            //Thread.Sleep(1000);
        }

        ~usbControl() {
            this.Dispose();
        }
    }
}
