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

    require_once('lib/syndms.client.php');

    /**
     * elogin_syno_shareprovider_bo
     */
    class elogin_syno_shareprovider_bo extends elogin_shareprovider_bo {

        protected $_syno = null;

        /**
         * constructor
         * @param string $id
         */
        public function __construct($id=null) {}

        /**
         * _construct2
         */
        protected function _construct2() {
            if( $this->_account_server != null ) {
                $this->_syno = new SyndmsClient(
                    $this->_account_server,
                    $this->_account_port
                    );

                if( $this->_syno->login($this->_account_user, $this->_account_password) ) {
                    // TODO
                }
            }
        }

        /**
         * getShares
         *
         * @return array
         */
        public function getShares() {
            if( $this->_syno ) {
                $shares = $this->_syno->getShares();

                if( is_array($shares) ) {
                    return $shares;
                }
            }

            return array();
        }

        /**
         * getSharesByUser
         *
         * @param string|elogin_usershares_bo $account
         * @return array
         */
        public function getSharesByUser($account) {
            if( $this->_syno ) {
                $username =  null;

                if( $account instanceof elogin_usershares_bo ) {
                    $username = $account->getUsername();
                }
                elseif( is_string($account) ) {
                    $username = $account;
                }

                if( $username ) {
                    $shares = $this->_syno->getUserShares($username);

                    if( is_array($shares) ) {
                        return $shares;
                    }
                }
            }

            return array();
        }

        /**
         * isUsernameExist
         *
         * @param string $username
         * @return boolean
         */
        public function isUsernameExist($username=null) {
            if( $username == null ) {
                $username = $this->_username;
            }

            if( $this->_syno ) {
                $user = $this->_syno->getUser($username);

                if( $user ) {
                    return true;
                }
            }

            return false;
        }

        /**
         * createUserShares
         *
         * @param int|elogin_usershares_bo $account
         */
        public function createUserShares($account) {
            //var_dump($account);
            if( $this->_syno ) {
                if( $account instanceof elogin_usershares_bo ) {
                    $usershares = $account;
                }
                else {
                    $usershares = parent::createUserShares($account);
                }

                if( $usershares->getId() != '' ) {
                    $this->_syno->createUser(
                        $usershares->getUsername(),
                        $usershares->getSharePassword()
                        );

                    return $usershares;
                }
                else {
                    //var_dump($usershares);
                }
            }

            return null;
        }

        /**
         * disableUserShares
         *
         * @param string|elogin_usershares_bo $account
         */
        public function disableUserShares($account) {
            if( $this->_syno ) {
                $username =  null;

                if( $account instanceof elogin_usershares_bo ) {
                    $username = $account->getUsername();
                }
                elseif( is_string($account) ) {
                    $username = $account;
                }

                if( $username ) {
                    return $this->_syno->disableUser($username);
                }
            }

            return false;
        }

        /**
         * enableUserShares
         *
         * @param string|elogin_usershares_bo $account
         */
        public function enableUserShares($account) {
            if( $this->_syno ) {
                $username =  null;

                if( $account instanceof elogin_usershares_bo ) {
                    $username = $account->getUsername();
                }
                elseif( is_string($account) ) {
                    $username = $account;
                }

                if( $username ) {
                    return $this->_syno->enableUser($username);
                }
            }

            return false;
        }

        /**
         * disableUserShares
         *
         * @param string|elogin_usershares_bo $account
         */
        public function isUserSharesDisabled($account) {
            if( $this->_syno ) {
                $username =  null;

                if( $account instanceof elogin_usershares_bo ) {
                    $username = $account->getUsername();
                }
                elseif( is_string($account) ) {
                    $username = $account;
                }

                if( $username ) {
                    return $this->_syno->isUserDisabled($username);
                }
            }

            return false;
        }

        /**
         * updatePassword
         *
         * @param elogin_usershares_bo $account
         */
        public function updatePassword($account) {
            if( $this->_syno ) {
                if( $account instanceof elogin_usershares_bo ) {
                    $username = $account->getUsername();
                    $password = $account->getSharePassword();

                    return $this->_syno->setUserPassword($username, $password);
                }
            }

            return false;
        }

        /**
         * createShare
         *
         * @param elogin_usershares_bo $account
         * @param string $sharename
         * @return boolean
         */
        public function createShare($account, $sharename) {
            if( $this->_syno ) {
                if( $account instanceof elogin_usershares_bo ) {
                    $username = $account->getUsername();

                    if( $this->_syno->createShare($sharename, '/volume1') ) {
                        return true;
                    }
                }
            }

            return false;
        }

        /**
         * setSharePermission
         *
         * @param elogin_usershares_bo $account
         * @param string $sharename
         * @param string $premission
         */
        public function setSharePermission($account, $sharename, $premission="rw") {
            if( $this->_syno ) {
                if( $account instanceof elogin_usershares_bo ) {
                    $username = $account->getUsername();

                    if( $this->_syno->setSharePermission($sharename, $username, $premission) ) {
                        return true;
                    }
                }
            }

            return false;
        }
    }