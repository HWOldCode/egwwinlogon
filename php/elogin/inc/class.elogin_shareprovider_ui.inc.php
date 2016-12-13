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
	use EGroupware\Api\Etemplate;

    /**
     * elogin_shareprovider_ui
     */
    class elogin_shareprovider_ui {

        /**
         * public methode
         * @var array
         */
        public $public_functions = array(
            'share_provider_list'       => true,
            'get_rows_shareprovider'    => true,
            'share_provider_edit'       => true,
            );

        /**
         * share_provider_list
         * @param array $content
         */
        public function share_provider_list($content=null) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

            $readonlys = array();

            if( !is_array($content) ) {
                if( !($content['nm'] = Api\Cache::getSession('elogin_shareprovider_list', 'elogin')) ) {
					$content['nm'] = array(		// I = value set by the app, 0 = value on return / output
						'get_rows'      =>	'elogin.elogin_shareprovider_ui.get_rows_shareprovider',	// I  method/callback to request the data for the rows eg. 'notes.bo.get_rows'
						'no_filter'     => true,// I  disable the 1. filter
						'no_filter2'    => true,// I  disable the 2. filter (params are the same as for filter)
						'no_cat'        => false,// I  disable the cat-selectbox
						//'never_hide'    => true,// I  never hide the nextmatch-line if less then maxmatch entrie
						'row_id'        => 'el_unid',
						'actions'       => self::index_get_actions(),
                        'header_row'    => 'elogin.shareprovider_list.header_right',
                        'favorites'     => false
						);
				}
			}

            $tpl = new Etemplate('elogin.shareprovider_list');
			$tpl->exec(
                'elogin.elogin_shareprovider_ui.share_provider_list',
                $content,
                array(),
                $readonlys,
                array(),
                0);
        }

        /**
         * index_get_actions
         * @param array $query
         * @return array
         */
        static public function index_get_actions($query=array()) {
            $group = 0;

            $actions = array(
                'edit' => array(
                    'caption'	=> 'Edit',
                    'group'		=> $group,
                    'default'	=> true,
                    'icon'		=> 'edit',
                    'hint'		=> 'Edit ShareProvider',
                    'enabled'	=> true,
                    'url'       => 'menuaction=elogin.elogin_shareprovider_ui.share_provider_edit&uid=$id',
                    'popup'     => '600x425',//egw_link::get_registry('elogin', 'add_popup'),
                    ),
                );

            return $actions;
        }

        /**
         * get_rows_shareprovider
         * @param array $query
         * @param array $rows
         * @param array $readonlys
         * @return int
         */
        public function get_rows_shareprovider(&$query, &$rows, &$readonlys) {
            Api\Cache::setSession('elogin_shareprovider_list', 'elogin', $query);

            $count = elogin_shareprovider_bo::get_rows(
				$query, $rows, $readonlys);

            foreach( $rows as &$row ) {
                $row['icon'] = 'provider.png';

				if( !isset($row['el_activ']) ) {
					$row['el_activ'] = lang('Disable');
				}

				if( $row['el_activ'] == '1' ) {
					$row['el_activ'] = lang('Enable');
				}
				else {
					$row['el_activ'] = lang('Disable');
				}
            }

            return $count;
        }

        /**
         * share_provider_edit
         * @param array $content
         */
        public function share_provider_edit($content=null) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

            if( $content == null ) {
                $content = array();
            }

            $uid = ( isset($content['uid']) ? $content['uid'] : null);
			$uid = ( $uid == null ? (isset($_GET['uid']) ? $_GET['uid'] : null) : $uid);

            $preserv    = array();
            $option_sel = array();
            $readonlys  = array();

            $provider		= null;
			$cast_provider	= null;

            if( $uid ) {
                $provider = new elogin_shareprovider_bo($uid);

                $preserv['uid'] = $uid;
            }

			// -----------------------------------------------------------------

            if( isset($content['button']) && isset($content['button']['apply']) ) {
                $content['button']['save'] = "pressed";
            }

             // save
            if( isset($content['button']) && isset($content['button']['save']) ) {
                if( !($provider instanceof elogin_shareprovider_bo) ) {
                    $provider = new elogin_shareprovider_bo();
                }

				$provider->setDescription($content['description']);
                $provider->setProviderName($content['provider']);
                $provider->setAccount(
                    $content['account_server'],
                    intval($content['account_port']),
                    $content['account_user'],
                    $content['account_password']
                    );

                $provider->setMountAddress($content['mount_address']);

				$isActiv = false;

				if( isset($content['activ']) && ($content['activ'] == '1') ) {
					$isActiv = true;
				}

				$provider->setIsActiv($isActiv);

				if( isset($content['protocol']) && ($content['protocol'] !== '') ) {
					$provider->setProtocol($content['protocol']);
				}

				if( isset($content['apiversion']) && ($content['apiversion'] !== '') ) {
					$provider->setApiVersion($content['apiversion']);
				}

                $provider->save();

				$cast_provider = elogin_shareprovider_bo::i($provider->getId());

				if( $cast_provider instanceof elogin_shareprovider_bo ) {
					if( !$cast_provider->login() ) {
						egw_framework::message(
							lang('Login faild by: ' . $provider->getProviderName()), 'warning');
					}
					else {
						egw_framework::message(
							lang('Login success by: ' . $provider->getProviderName()), 'info');
					}
				}
            }
            elseif( isset($content['button']) && isset($content['button']['delete']) ) {
				
            }

			// -----------------------------------------------------------------

            if( $provider ) {
				$content['description']			= $provider->getDescription();
                $content['provider']            = $provider->getProviderName();
                $content['account_server']      = $provider->getAccountServer();
                $content['account_port']        = $provider->getAccountPort();
                $content['account_user']        = $provider->getAccountUser();
                $content['account_password']    = $provider->getAccountPassword();
                $content['mount_address']       = $provider->getMountAddress();
				$content['activ']				= ($provider->isActiv() ? '1' : '0');

				$cast_provider = elogin_shareprovider_bo::i($provider->getId());

				if( $cast_provider instanceof elogin_shareprovider_bo ) {
					$content['protocol']		= '';
					$content['apiversion']		= '';

					$option_sel['protocol']	= $cast_provider->getProtocolNames();

					if( $option_sel['protocol'] !== null ) {
						$content['protocol'] = $cast_provider->getProtocol();
					}

					$option_sel['apiversion'] = $cast_provider->getApiVersions();

					if( $option_sel['apiversion'] !== null ) {
						$content['apiversion'] = $cast_provider->getApiVersion();
					}
				}
				else {
					$readonlys['protocol']		= true;
					$readonlys['apiversion']	= true;
				}
            }

			// -----------------------------------------------------------------

            $option_sel['provider'] =
				elogin_shareprovider_bo::getShareProviderNames();

			$option_sel['activ'] = array(
				'1' => 'Enable',
				'0' => 'Disable'
				);

			// -----------------------------------------------------------------

            $etemplate = new Etemplate('elogin.share_provider.dialog');
            $etemplate->exec(
                    'elogin.elogin_shareprovider_ui.share_provider_edit',
                    array_merge($content, $preserv),
                    $option_sel,
                    $readonlys,
                    $preserv,
                    2);
        }
    }