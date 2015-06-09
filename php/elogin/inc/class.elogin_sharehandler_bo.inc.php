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
     * elogin_sharehandler_bo
     */
    class elogin_sharehandler_bo {

        /**
         * set_async_job
         *
         * @param boolean $start
         */
        static function set_async_job($start=true) {
            $async = new asyncservice();

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
         *
         * @param string $provider_id
         */
        static public function handle($provider_id=null) {
            $provider_list = array();

            if( $provider_id == null ) {
                $query = array();
                $rows = array();
                $readonlys = array();

                elogin_shareprovider_bo::get_rows($query, $rows, $readonlys);

                foreach( $rows as $trow ) {
                    $tprovider = elogin_shareprovider_bo::i($trow['el_unid']);

                    if( $tprovider instanceof elogin_shareprovider_bo ) {
                        $provider_list[] = $tprovider;
                    }
                }
            }
            else {
                $tprovider = elogin_shareprovider_bo::i($provider_id);

                if( $tprovider instanceof elogin_shareprovider_bo ) {
                    $provider_list[] = $tprovider;
                }
            }
var_dump('Providerlist: ' . count($tprovider) );
            // -----------------------------------------------------------------

            $egw_accounts = elogin_bo::getEgroupwareAccounts();
            $ignoruser = array(
                'admin',
                'sysop',
                'anonymous'
                );

            foreach( $provider_list as $provider ) {
                foreach( $egw_accounts as $egw_account ) {
        echo "<hr>";
                    if( in_array($egw_account['account_lid'], $ignoruser) ) {
                        continue;
                    }

                    $provider_id    = $provider->getId();
                    $accid          = $egw_account['account_id'];
                    $username       = $egw_account['account_lid'];
                    $isExist        = $provider->isUsernameExist($username);

        var_dump("User Exist: " . $username . " Exist: " . ($isExist ? 'ja' : 'nein') . "<br>");

                    $usershares     = null;

                    $usuid          = elogin_usershares_bo::existByAccountAndProvider($accid, $provider_id);

                    if( $egw_account['account_status'] == 'A' ) {
                var_dump("Aktiv");
                //var_dump($usuid);
                        if( !$isExist ) {
                            if( !($usuid) ) {
                                $usershares = $provider->createUserShares($accid);
                            }
                            else {
                                $usershares = $provider->createUserShares(new elogin_usershares_bo($usuid));
                            }

                            $isExist = $provider->isUsernameExist($username);

                            if( !$isExist ) {
                                echo "<b>nicht angelegt: $username!</b>";
                            }
                //var_dump($isExist);
                        }
                    }

                    // read usershares
                    if( ($usuid) && ($usershares == null)  ) {
                        $usershares = new elogin_usershares_bo($usuid);
                    }

                    // update account
                    if( $usershares ) {
            //var_dump($usershares->getUsername());
                        // disable account
                        if( ($egw_account['account_status'] <> 'A') && $isExist ) {
                            if( $usershares->getProvider()->disableUserShares($usershares) ) {
                                // success
                                var_dump("Disable");
                            }

                            var_dump("Disable Ende");
                        }
                        elseif( $isExist ) {
            var_dump("Account vorhanden");
                            // enable account
                            if( $usershares->getProvider()->isUserSharesDisabled($usershares) ) {

                                if( $usershares->getProvider()->enableUserShares($usershares) ) {
                                    var_dump("Enable");
                                }

                                var_dump("Enable Ende");
                            }

                            // update account, new password?
                            if( $usershares->getProvider()->updatePassword($usershares) ) {
                                // success
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

                            // -----

                            foreach( $kdshares as $tname => $tdshare ) {
                                if( isset($kushares[$tname]) ) {
                                    continue;
                                }

                                // create share only when not exist
                                if( !isset($kshares[$tname]) ) {
                                    if( $usershares->getProvider()->createShare($usershares, $tname) ) {
                                        // todo
                                        var_dump("Share is set.");
                                    }
                                }

                                // add access to share
                                if( $usershares->getProvider()->setSharePermission($usershares, $tname) ) {
                                    var_dump("Share access is set.");
                                }
                            }

                            // -----
                            // update mount list
                        //var_dump("Update Mounts");
                            $usershares->updateUserSharesMounts();
                        }
                    }
                }
            }
        }
    }
