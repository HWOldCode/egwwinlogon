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
     * elogin_usershares_ui
     */
    class elogin_usershares_ui {

        /**
         * public methode
         * @var array
         */
        public $public_functions = array(
            'share_user_list'       => true,
            'get_rows_shareuser'    => true,
            'share_user_edit'       => true,
            );

        /**
         * share_user_list
         *
         * @param array $content
         */
        public function share_user_list($content) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

            $readonlys = array();

            if( !is_array($content) ) {
                if( !($content['nm'] = egw_session::appsession('elogin_shareuser_list', 'elogin')) ) {
					$content['nm'] = array(		// I = value set by the app, 0 = value on return / output
						'get_rows'      =>	'elogin.elogin_usershares_ui.get_rows_shareuser',	// I  method/callback to request the data for the rows eg. 'notes.bo.get_rows'
						'no_filter'     => true,// I  disable the 1. filter
						'no_filter2'    => true,// I  disable the 2. filter (params are the same as for filter)
						'no_cat'        => false,// I  disable the cat-selectbox
						//'never_hide'    => true,// I  never hide the nextmatch-line if less then maxmatch entrie
						'row_id'        => 'el_unid',
						'actions'       => self::index_get_actions(),
                        'header_row'    => 'elogin.shareuser_list.header_right',
                        'favorites'     => false
						);
				}
			}

            $tpl = new etemplate_new('elogin.shareuser_list');
			$tpl->exec(
                'elogin.elogin_usershares_ui.share_user_list',
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
                    'hint'		=> 'Edit Usershares',
                    'enabled'	=> true,
                    'url'       => 'menuaction=elogin.elogin_usershares_ui.share_user_edit&uid=$id',
                    'popup'     => '600x425',//egw_link::get_registry('elogin', 'add_popup'),
                    ),
                );

            return $actions;
        }

        /**
         * get_rows_shareuser
         *
         * @param type $query
         * @param type $rows
         * @param type $readonlys
         * @return type
         */
        public function get_rows_shareuser(&$query, &$rows, &$readonlys) {
            egw_session::appsession('elogin_shareuser_list', 'elogin', $query);

            $count = elogin_usershares_bo::get_rows($query, $rows, $readonlys);

            foreach( $rows as &$trow ) {
                $t = new elogin_usershares_bo($trow['el_unid']);

                if( $t ) {
                    $provider = $t->getProvider(true);

                    if( $provider ) {
                        $trow['provider_name']  = $provider->getProviderName();
                    }

                    $trow['username']       = $t->getUsername();
                    $trow['password']       = $t->getSharePassword();
                }

                $trow['icon']       = 'usershare.png';
            }

            return $count;
        }

        /**
         * share_user_edit
         *
         * @param array $content
         */
        public function share_user_edit($content=null) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }
            
            if( $content == null ) {
                $content = array();
            }

            $preserv    = array();
            $option_sel = array();
            $readonlys  = array();

            $uid = ( isset($content['uid']) ? $content['uid'] : null);
			$uid = ( $uid == null ? (isset($_GET['uid']) ? $_GET['uid'] : null) : $uid);

            $t = new elogin_usershares_bo($uid);

            if( $t->getId() != '' ) {
                $content['provider']        = $t->getProvider()->getProviderName();
                $content['user']            = $t->getUsername();
                $content['sharepassword']   = $t->getSharePassword();

                var_dump($t->getCmds());
            }

            $readonlys['user']      = true;
            $readonlys['provider']  = true;

            $etemplate = new etemplate_new('elogin.share_user.dialog');
            $etemplate->exec(
                'elogin.elogin_usershares_ui.share_user_edit',
                array_merge($content, $preserv),
                $option_sel,
                $readonlys,
                $preserv,
                2);
        }
    }