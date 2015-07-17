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
         *
         * @param array $content
         */
        public function share_provider_list($content=null) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

            $readonlys = array();

            if( !is_array($content) ) {
                if( !($content['nm'] = egw_session::appsession('elogin_shareprovider_list', 'elogin')) ) {
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

            $tpl = new etemplate_new('elogin.shareprovider_list');
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
         *
         * @param array $query
         * @return array
         */
        static public function index_get_actions($query=array()) {
            $group = 1;

            $actions = array(
                'edit' => array(
                    'caption'	=> 'Edit',
                    'group'		=> $group,
                    'default'	=> false,
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
         *
         * @param type $query
         * @param type $rows
         * @param type $readonlys
         * @return type
         */
        public function get_rows_shareprovider(&$query, &$rows, &$readonlys) {
            egw_session::appsession('elogin_shareprovider_list', 'elogin', $query);

            $count = elogin_shareprovider_bo::get_rows($query, $rows, $readonlys);

            foreach( $rows as &$row ) {
                $row['icon'] = 'provider.png';
            }

            return $count;
        }

        /**
         * share_provider_edit
         *
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

            $provider = null;

            if( $uid ) {
                $provider = new elogin_shareprovider_bo($uid);

                $preserv['uid'] = $uid;
            }

            if( isset($content['button']) && isset($content['button']['apply']) ) {
                $content['button']['save'] = "pressed";
            }

             // save
            if( isset($content['button']) && isset($content['button']['save']) ) {
                if( !($provider instanceof elogin_shareprovider_bo) ) {
                    $provider = new elogin_shareprovider_bo();
                }

                $provider->setProviderName($content['provider']);
                $provider->setAccount(
                    $content['account_server'],
                    intval($content['account_port']),
                    $content['account_user'],
                    $content['account_password']
                    );

                $provider->setMountAddress($content['mount_address']);
                $provider->save();


                /*egw_framework::refresh_opener(
                    'ShareProvider Save',
                    'elogin',
                    $provider->getId(),
                    'save'
                    );*/

                egw::redirect_link(egw::link('/index.php', array(
                    'menuaction' => 'elogin.elogin_shareprovider_ui.share_provider_edit',
                    'uid' => $provider->getId()
                    )));
            }
            elseif( isset($content['button']) && isset($content['button']['delete']) ) {

            }

            if( $provider ) {
                $content['provider']            = $provider->getProviderName();
                $content['account_server']      = $provider->getAccountServer();
                $content['account_port']        = $provider->getAccountPort();
                $content['account_user']        = $provider->getAccountUser();
                $content['account_password']    = $provider->getAccountPassword();
                $content['mount_address']       = $provider->getMountAddress();

            }

            $option_sel['provider'] = elogin_shareprovider_bo::getShareProviderNames();

            $etemplate = new etemplate_new('elogin.share_provider.dialog');
            $etemplate->exec(
                    'elogin.elogin_shareprovider_ui.share_provider_edit',
                    array_merge($content, $preserv),
                    $option_sel,
                    $readonlys,
                    $preserv,
                    2);
        }
    }