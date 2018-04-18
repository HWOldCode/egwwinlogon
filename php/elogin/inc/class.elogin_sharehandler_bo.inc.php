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

	use EGroupware;
	use EGroupware\Api;

    /**
     * elogin_sharehandler_bo
     */
    class elogin_sharehandler_bo {

		/**
		 * logging
		 * @var boolean
		 */
		static protected $_logging = false;

		/**
         * set_async_job
         * @param boolean $start
         */
        static function set_async_job($start=true) {
			// TODO $async = new Api\AsyncService(); ???
            $async = $GLOBALS['egw']->asyncservice;

            if( $start === !$async->read('elogin-sharehandler') ) {
                if( $start ) {
                    $async->set_timer(
                        array('hour' => '*'),
                        'elogin-sharehandler',
                        'elogin.elogin_sharehandler_bo.handle',
                        null
                        );
                }
                else {
                    $async->cancel_timer('elogin-sharehandler');
                }
            }
        }

        /**
         * handle
         * @param string $provider_id
         */
        static public function handle($provider_id=null) {
			$conjobfile = $GLOBALS['egw_info']['server']['temp_dir'] . '/elogin_sharehandler.tmp';

			self::cronjob_error_log($conjobfile, __LINE__);

			$cache_life = 60 * 60 * 6;

			$isExec = false;

			if( file_exists($conjobfile) ) {
				$filemtime = @filemtime($conjobfile);

				if( (!$filemtime) || (time() - $filemtime >= $cache_life)) {
					unlink($conjobfile);
					$isExec = true;
				}
			}
			else {
				$isExec = true;
			}

			/*if( $isExec ) {
				file_put_contents($conjobfile, '1');
			}
			else {
				return;
			}*/

			// -----------------------------------------------------------------

			try {
				$provider_list = array();

				if( $provider_id == null ) {
					$query = array();
					$rows = array();
					$readonlys = array();

					elogin_shareprovider_bo::get_rows($query, $rows, $readonlys);

					foreach( $rows as $trow ) {
						try {
							$tprovider = elogin_shareprovider_bo::i($trow['el_unid']);

							if( $tprovider instanceof elogin_shareprovider_bo ) {
								$provider_list[] = $tprovider;
							}
						}
						catch( Exception $e ) {
							self::cronjob_error_log($e, $e->getLine());
						}
					}
				}
				else {
					$tprovider = elogin_shareprovider_bo::i($provider_id);

					if( $tprovider instanceof elogin_shareprovider_bo ) {
						$provider_list[] = $tprovider;
					}
				}

				self::cronjob_error_log('Providerlist: ' . count($tprovider), __LINE__);

				// -----------------------------------------------------------------

				$egw_accounts = elogin_bo::getEgroupwareAccounts();
				$ignoruser = array(
					'admin',
					'sysop',
					'anonymous'
					);

				foreach( $provider_list as $provider ) {
					if( !$provider->isLogin() ) {
						self::cronjob_error_log($provider->getProviderName() . ": <b>nicht eingelogt!</b><br>", __LINE__);
						continue;
					}
					else {
						self::cronjob_error_log("Eingelogt: " . $provider->getProviderName() . "<br><br>", __LINE__);
					}

					$circle_count = 1;

					foreach( $egw_accounts as $egw_account ) {
						if( in_array($egw_account['account_lid'], $ignoruser) ) {
							continue;
						}

						if( $circle_count%6 ) {
							$provider->logout();
							$provider->login();

							if( !$provider->isLogin() ) {
								var_dump("Fehler beim relogin!");
								break;
							}
							else {
								var_dump("relogin!");
							}
						}

						$circle_count++;

						self::cronjob_error_log('-----------------------------------', __LINE__);

						$provider_id    = $provider->getId();
						$accid          = $egw_account['account_id'];
						$username       = $egw_account['account_lid'];
						$isExist        = $provider->isUsernameExist($username);

						self::cronjob_error_log("User Exist: " . $username .
							" Exist: " . ($isExist ? 'ja' : 'nein') . "<br>", __LINE__);

						$usershares     = null;

						$usuid          = elogin_usershares_bo::existByAccountAndProvider($accid, $provider_id);

						if( $egw_account['account_status'] == 'A' ) {
							self::cronjob_error_log("Aktiv", __LINE__);

							if( !$isExist ) {
								if( !($usuid) ) {
									$usershares = $provider->createUserShares($accid);
								}
								else {
									$usershares = $provider->createUserShares(new elogin_usershares_bo($usuid));
								}

								$isExist = $provider->isUsernameExist($username);

								if( !$isExist ) {
									self::cronjob_error_log("<b>nicht angelegt: $username!</b>");
								}
							}
						}

						// read usershares
						if( ($usuid) && ($usershares == null)  ) {
							$usershares = new elogin_usershares_bo($usuid);
						}

						// update account
						if( $usershares ) {
							// disable account
							if( ($egw_account['account_status'] <> 'A') && $isExist ) {
								if( $usershares->getProvider()->disableUserShares($usershares) ) {
									// success
									self::cronjob_error_log("Disable", __LINE__);
								}

								self::cronjob_error_log("Disable Ende", __LINE__);
							}
							elseif( $isExist ) {
								self::cronjob_error_log("Account vorhanden", __LINE__);

								// enable account
								if( $usershares->getProvider()->isUserSharesDisabled($usershares) ) {

									if( $usershares->getProvider()->enableUserShares($usershares) ) {
										var_dump("Enable");
									}

									self::cronjob_error_log("Enable Ende", __LINE__);
								}

								// update account, new password?
								if( $usershares->getProvider()->updatePassword($usershares) ) {
									// success
									self::cronjob_error_log("Password erfolgreich geupdatet.", __LINE__);
								}
								else {
									self::cronjob_error_log("Password nicht geupdatet. (Fehler)", __LINE__);
								}

								// shares setting
								$shares = $usershares->getProvider()->getShares($usershares);
								$ushares = $usershares->getShares();
								$dshares = $usershares->getDefaultShares();

								// --- transform arrays
								$kshares = array();
								$kushares = array();
								$kdshares = array();

								foreach( $shares as $tshare ) {
									$kshares[$tshare['name']] = $tshare;
								}

								foreach( $ushares as $tshare ) {
									$kushares[$tshare['name']] = $tshare;
								}

								foreach( $dshares as $tshare ) {
									$kdshares[$tshare['name']] = $tshare;
								}

								//var_dump($kshares);
								// -----

								foreach( $kdshares as $tname => $tdshare ) {
									if( isset($kushares[$tname]) ) {
										continue;
									}

									// create share only when not exist
									if( !isset($kshares[$tname]) ) {
										if( $usershares->getProvider()->createShare($usershares, $tname) ) {
											// todo
											self::cronjob_error_log("Share is set.", __LINE__);
										}
									}

									// add access to share
									if( $usershares->getProvider()->setSharePermission($usershares, $tname) ) {
										self::cronjob_error_log("Share access is set.", __LINE__);
									}
								}

								// -----
								// update mount list
							//var_dump("Update Mounts");
								$usershares->updateUserSharesMounts();
							}
						}
					}

					$provider->logout();
				}
			}
			catch( Exception $ex ) {
				self::cronjob_error_log($ex, $ex->getLine());

				try {
					if( $provider !== null ) {
						$provider->logout();
					}
				}
				catch (Exception $ex2) {
					self::cronjob_error_log($ex2, $ex2->getLine());
				}
			}

			@unlink($conjobfile);
        }

		/**
		 * cronjob_error_log
		 * @param type $message
		 * @param type $line
		 */
		static public function cronjob_error_log($message, $line) {
			if( !self::$_logging ) {
				return;
			}

			if( is_array($message) ) {
				$message = var_export($message, true);
			}

			$message = 'Line: ' . $line . ' Message: ' . $message . "\r\n";

			$file = $GLOBALS['egw_info']['server']['temp_dir'] . '/elogin_sharehandler.log';

			error_log($message, 3, $file);
		}
    }
