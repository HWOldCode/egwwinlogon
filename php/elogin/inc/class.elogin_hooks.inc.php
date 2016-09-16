<?php

    /**
	 * ELogin - Egroupware
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-16 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

	use EGroupware\Api;

	/**
	 * elogin_hooks
	 */
    class elogin_hooks {

		/**
		 * constante
		 */
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
            static::$_config = Api\Config::read(self::APP);
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
                    $file['Machine List']			= Api\Egw::link('/index.php', 'menuaction=' . $appname . '.elogin_machine_ui.machine_list&ajax=true');
                    $file['Machine-logging List']	= Api\Egw::link('/index.php', 'menuaction=' . $appname . '.elogin_machine_logging_ui.logging_list&ajax=true');
                    $file['Share Provider']			= Api\Egw::link('/index.php', 'menuaction=' . $appname . '.elogin_shareprovider_ui.share_provider_list&ajax=true');
                    $file['Cronjob by Hand']		= Api\Egw::link('/index.php', 'menuaction=' . $appname . '.elogin_ui.cronjob_hand&ajax=true');
					$file['Link List']				= Api\Egw::link('/index.php', 'menuaction=' . $appname . '.elogin_link_ui.link_list&ajax=true');
                }

                $file['Share User'] = Api\Egw::link('/index.php', 'menuaction=' . $appname . '.elogin_usershares_ui.share_user_list&ajax=true');

				display_sidebox($appname, 'elogin', $file);
            }
        }

        /**
         * admin
         * @param string|array $data hook-data
         */
        public static function admin($data) {

        }

		/**
		 * search_link
		 * @param string $location
		 * @return array
		 */
		static public function search_link($location) {
			$appname = self::APP;

			return array(
				'query'					=> $appname . '.elogin_machine_bo.link_query',
				'title'					=> $appname . '.elogin_machine_bo.link_title',
				'titles'				=> $appname . '.elogin_machine_bo.link_titles',
				'entry'					=> 'ELogin-Machine',
				'additional'			=> array(
					'elogin-cmd'		=> array(
                            'query'		=> $appname . '.elogin_cmd_bo.link_query',
                            'title'		=> $appname . '.elogin_cmd_bo.link_title',
                            'titles'	=> $appname . '.elogin_cmd_bo.link_titles',
							'entry'		=> 'ELogin-Cmd',
						),
					'elogin-link'		=> array(
							'query'		=> $appname . '.elogin_link_bo.link_query',
                            'title'		=> $appname . '.elogin_link_bo.link_title',
                            'titles'	=> $appname . '.elogin_link_bo.link_titles',
							'entry'		=> 'ELogin-Link',
							'view'			=> array(
								'menuaction' => $appname . '.elogin_link_ui.open',
							),
							'view_id'		=> 'unid',
							'view_popup'	=> '750x580',
						),
					)
				);
		}

        /**
		 * settings
		 * @param mixed $hook_data
		 * @return array
		 */
		static function settings($hook_data=null) {
            $settings = array(
                '1.section' => array(
                    'type'      => 'section',
                    'title'     => lang('ELogin Client Settings (Windows)'),
                    'no_lang'   => true,
                    'xmlrpc'    => false,
                    'admin'     => false
                ),
                'set_login_credential_img' => array(
                    'type'      => 'input',
                    'size'      => 256,
                    'label'     => 'Set registry, credential login image',
                    'name'      => 'set_login_credential_img',
                    'forced'    => 'C:\\Program Files\\pGina\\titleimage.bmp',
                    'help'     => 'Set registry, credential login image',
                    'xmlrpc'    => true,
                    'admin'     => false,
                ),
                'enable_show_trays' => array(
                    'type'      => 'check',
                    'label'     => 'Set registry, enable all trays show in notifactions area',
                    'name'      => 'enable_show_trays',
                    'help'      => 'Set registry, enable all trays show in notifactions area',
                    'xmlrpc'    => true,
                    'admin'     => false,
                    'forced'    => '1',
                ),
            );

            return $settings;
        }
    }

    /**
     * init
     */
    elogin_hooks::init();