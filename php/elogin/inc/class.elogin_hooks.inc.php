<?php

    /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-14 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id:$
	 */

    class elogin_hooks {

        const APP = 'elogin';


        /**
         * config
         * @var array
         */
        private static $_config = array();

        /**
		 * init
		 */
		static public function init() {
            self::$_config = config::read(self::APP);
        }

        /**
		 * all_hooks
         * hooks to build eworkflow's sidebox-menu plus the admin and preferences sections
		 *
         * @param string/array $args hook args
         */
        static public function all_hooks($args) {
            $appname = self::APP;
            $location = (is_array($args) ? $args['location'] : $args);

            // Sidebox Menu ----------------------------------------------------

			if( $location == 'sidebox_menu' ) {

                $file = array();

                /**
                 * Admin
                 */
                if( $GLOBALS['egw_info']['user']['apps']['admin'] ) {
                    $file['Machine List'] = egw::link('/index.php', 'menuaction=' . $appname . '.elogin_machine_ui.machine_list&ajax=true');
                    $file['Machine-logging List'] = egw::link('/index.php', 'menuaction=' . $appname . '.elogin_machine_logging_ui.logging_list&ajax=true');
                    $file['Share Provider'] = egw::link('/index.php', 'menuaction=' . $appname . '.elogin_shareprovider_ui.share_provider_list&ajax=true');
                    $file['Cronjob by Hand'] = egw::link('/index.php', 'menuaction=' . $appname . '.elogin_ui.cronjob_hand&ajax=true');
                }

                $file['Share User']     = egw::link('/index.php', 'menuaction=' . $appname . '.elogin_usershares_ui.share_user_list&ajax=true');

				display_sidebox($appname, 'elogin', $file);
            }
        }

        /**
         * admin
         *
         * @param string|array $data hook-data
         */
        public static function admin($data) {

        }

        /**
		 * settings
		 *
		 * @param mixed $hook_data
		 * @return array
		 */
		static function settings($hook_data=null) {

        }
    }

    /**
     * init
     */
    elogin_hooks::init();