<?php

    /**
	 * ELogin - Egroupware
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-17 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

	use EGroupware\Api;
	use EGroupware\Api\Etemplate;

    /**
     * elogin_usershares_ui
     */
    class elogin_usershares_ui {

        /**
         * public methode
         * @var array
         */
        public $public_functions = array(
            'share_user_list'           => true,
            'get_rows_shareuser'        => true,
            'share_user_edit'           => true,
            'get_rows_shareuser_mount'  => true,
            'shareuser_mount_edit'      => true,
            'ajax_usershare_mount'      => true,
            );

        /**
         * share_user_list
         * @param array $content
         */
        public function share_user_list($content=null) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

            $readonlys = array();

            if( !is_array($content) ) {
                if( !($content['nm'] = Api\Cache::getSession('elogin_shareuser_list', 'elogin')) ) {
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

            $tpl = new Etemplate('elogin.shareuser_list');
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
         * @param array $query
         * @return array
         */
        static public function index_get_actions($query=array()) {
            $group = 1;

            $actions = array(
                'edit' => array(
                    'caption'	=> 'Edit',
                    'group'		=> $group,
                    'default'	=> true,
                    'icon'		=> 'edit',
                    'hint'		=> 'Edit Usershares',
                    'enabled'	=> true,
                    'url'       => 'menuaction=elogin.elogin_usershares_ui.share_user_edit&uid=$id',
                    'popup'     => '600x425',//egw_link::get_registry('elogin', 'add_popup'),
                    ),
                /*'usersahresmount' => array(
                    'caption'	=> 'Mount List',
                    'group'		=> $group,
                    'default'	=> false,
                    'icon'		=> 'share',
                    'hint'		=> 'Mount List by Usershares',
                    'enabled'	=> true,
                    'url'       => 'menuaction=elogin.elogin_usershares_ui.share_user_edit&uid=$id',
                    'popup'     => '600x425',//egw_link::get_registry('elogin', 'add_popup'),
                    )*/
                );

            return $actions;
        }

        /**
         * get_rows_shareuser
         * @param array $query
         * @param array $rows
         * @param array $readonlys
         * @return int
         */
        public function get_rows_shareuser(&$query, &$rows, &$readonlys) {
            Api\Cache::setSession('elogin_shareuser_list', 'elogin', $query);

            $count = elogin_usershares_bo::get_rows($query, $rows, $readonlys);

            foreach( $rows as &$trow ) {
                $t = new elogin_usershares_bo($trow['el_unid']);

                if( $t ) {
                    $provider = $t->getProvider(true);

                    if( $provider ) {
                        $trow['provider_name']  = $provider->getDescription() .
							'(' . $provider->getProviderName() . ')';

						$trow['provider_ip']	= $provider->getAccountServer();
                    }

                    $trow['username'] = $t->getUsername();
                    $trow['password'] = $t->getSharePassword();
                }

                $trow['icon'] = 'usershare.png';
            }

            return $count;
        }

        /**
         * share_user_edit
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

			$t = null;

			if( $uid !== null ) {
				$t = new elogin_usershares_bo($uid);
			}

			// write -----------------------------------------------------------
			if( isset($content['button']) && (isset($content['button']['save']) ||
				isset($content['button']['apply'])) )
			{
				$isAdd = false;

				if( $t == null ) {
					$isAdd	= true;
					$t		= new elogin_usershares_bo();
				}

				$t->setProviderId($content['providers']);
				$t->setUser($content['user']);
				$t->setSharePassword($content['sharepassword']);
				$t->save();

				egw_framework::refresh_opener(
					lang('User share') . ' ' . ($isAdd ? 'add' : 'update'),
					'elogin',
					$t->getId(),
					($isAdd ? 'add' : 'edit'),
					null
					);

				// button action
				// -------------------------------------------------------------
				if( isset($content['button']['save']) ) {
					egw_framework::window_close();
					exit;
				}
			}

			// delete ----------------------------------------------------------
			//TODO

			// read ------------------------------------------------------------
			if( $t instanceof elogin_usershares_bo ) {
				if( $t->getId() != '' ) {
					$content['provider']		= $t->getProvider(true)->getDescription();
					$content['providers']       = $t->getProviderId();
					$content['user']            = $t->getUserId();
					$content['sharepassword']   = $t->getSharePassword();

					$preserv['uid'] = $t->getId();
				}
			}

			$option_sel['providers'] = array();

			foreach( elogin_shareprovider_bo::getShareProviders() as $provider ) {
				$name =  $provider->getDescription() .
						'(' . $provider->getProviderName() . ')';

				$option_sel['providers'][$provider->getId()] = $name;
			}

			try {
				//$t->updateUserSharesMounts();
			}
			catch( Exception $e ) {
				egw_framework::message(lang($e->getMessage()), 'warning');
			}

            $readonlys['provider']  = true;

            $etemplate = new Etemplate('elogin.share_user.dialog');
            $etemplate->exec(
                'elogin.elogin_usershares_ui.share_user_edit',
                array_merge($content, $preserv),
                $option_sel,
                $readonlys,
                $preserv,
                2);
        }

        /**
         * get_rows_shareuser_mount
         * @param array $query
         * @param array $rows
         * @param type $readonlys
         */
        public function get_rows_shareuser_mount(&$query, &$rows, &$readonlys) {
            if( key_exists('userschareunid', $query) ) {
                $query['col_filter'] = array(
                    'el_usershare_id' => $query['userschareunid']
                    );

                unset($query['userschareunid']);
            }

            $count = elogin_usershares_mount_bo::get_rows($query, $rows, $readonlys);

            return $count;
        }

        /**
         * shareuser_mount_edit
         * @param array $content
         */
        public function shareuser_mount_edit($content=null) {
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

            $etemplate = new Etemplate('elogin.share_user_mount.dialog');
            $etemplate->exec(
                'elogin.elogin_usershares_ui.shareuser_mount_edit',
                array_merge($content, $preserv),
                $option_sel,
                $readonlys,
                $preserv,
                2);
        }

        /**
         * ajax_usershare_mount
         * @param array $content
         */
        static public function ajax_usershare_mount($content=array()) {
            if( isset($content['mountid']) ) {
                if( $content['action'] == 'edit' ) {
                    $mount = new elogin_usershares_mount_bo($content['mountid']);
                    $mount->setMountname($content['mount_name']);
                    $mount->save();
                }
            }
        }

		/**
         * ajax_ns_list
         * @param array $content
         */
        public function ajax_ns_list($content=array()) {
			if( isset($content['uid']) ) {
                $machine = new elogin_machine_bo($content['uid']);

				if( $machine->getIsInDb() ) {
                    $usersahres = $machine->getCurrentUserShares();

					$list = array();

					foreach( $usersahres as $usershare ) {
						$mounts = array();

						foreach( $usershare->getUserSharesMounts() as $amount ) {
							$mounts[] = $amount->toArray();
						}

						$list[$usershare->getUsername()] = $mounts;
					}

					return Api\Json\Response::get()->data(array(
                        'status' => 'ok',
                        'ns' => $list));
				}
			}

			return Api\Json\Response::get()->data(
				array('status' => 'error'));
		}
    }