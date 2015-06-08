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

        /**
         * instances
         * @var array
         */
        static protected $_synoInstances = array();


        /**
         * client
         * @var SyndmsClient
         */
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
            //var_dump($this->_account_user);
            //var_dump($this->_account_password);

            if( $this->_account_server != null ) {
                if( isset(elogin_syno_shareprovider_bo::$_synoInstances[$this->_id]) ) {
                    //echo "Cache client<br>";
                    $this->_syno = elogin_syno_shareprovider_bo::$_synoInstances[$this->_id];
                }
                else {
                    //echo "Erzeuge client<br>";
                    $this->_syno = new SyndmsClient(
                        $this->_account_server,
                        $this->_account_port
                        );

                    elogin_syno_shareprovider_bo::$_synoInstances[$this->_id] = $this->_syno;
                }

                if( !$this->_syno->isLogin() ) {
                    //echo "nicht eingelogt client<br>";
                    if( $this->_syno->login($this->_account_user, $this->_account_password) ) {
                        // TODO
                        //echo "eingelogt client<br>";
                    }
                    else {
                       //echo "fehler login client<br>";
                    }
                }

                //exit;
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

        /**
         * isLogin
         *
         * @return boolean
         */
        public function isLogin() {
            if( $this->_syno ) {
                if( $this->_syno->isLogin() ) {
                    return true;
                }
            }

            return false;
        }

        /**
         * getShareDirList
         *
         * @param string $usersharename
         * @param string $dir
         * @return array
         */
        public function getShareDirList($usersharename, $dir="") {
            if( $this->_syno ) {
                $list = $this->_syno->getFileSharesList(
                    $usersharename,
                    1000,
                    array(
                        'only_dir' => true
                        )
                    );

                return $list;
            }

            return array();
        }

        /**
         * existShareDir
         *
         * @param string $usersharename
         * @param string $dir
         * @return boolean
         */
        public function existShareDir($usersharename, $dir) {
            if( $this->_syno ) {
                $list = $this->getShareDirList($usersharename);

                if( isset($list[$usersharename . '/' . $dir]) ) {
                    return true;
                }
            }

            return false;
        }

        /**
         * createShareDir
         *
         * @param string $usersharename
         * @param string $dir
         * @return boolean
         */
        public function createShareDir($usersharename, $dir) {
            if( $this->_syno ) {
                if( $this->_syno->createDirShare($usersharename, $dir) ) {
                    return true;
                }
            }

            return false;
        }

        /**
         * removeAllPermissionDir
         *
         * @param string $usersharename
         * @param string $dir
         * @return boolean
         */
        public function removeAllPermissionDir($usersharename, $dir) {
            if( $this->_syno ) {
                if( $this->_syno->setFileShareACLs('/volume1', $usersharename . $dir) ) {
                    return true;
                }
            }

            return false;
        }

        /**
         * addPermissionDir
         *
         * @param string $usersharename
         * @param string $dir
         * @param string $username
         * @param boolean $read
         * @param boolean $write
         * @return boolean
         */
        public function addPermissionDir($usersharename, $dir, $username, $read=false, $write=false) {
            if( $this->_syno ) {
                if( !$this->isUsernameExist($username) ) {
                    return false;
                }

                /*$shares = $this->_syno->getUserShares($username);
                $sharelist = array();

                foreach( $shares as $tshare ) {
                    if( $tshare[''])
                    $sharelist[] = $tshare['share_path'];
                }

                if( !in_array('/volume1' . $usersharename, $sharelist) ) {
                    return false;
                }
                */

                $list = $this->_syno->getFileShareACLs('/volume1' . $usersharename . $dir);

                $rules = array();

                foreach( $list as $tusername => $permission ) {
                    if( $tusername == $username ) {
                        continue;
                    }

                    $rules[] = array(
                        'owner_type'        => 'user',
                        'owner_name'        => $tusername,
                        'permission_type'   => 'allow',
                        'permission'        => $permission,
                        'inherit'           => array(
                            'child_files'   => true,
                            'child_folders' => true,
                            'this_folder'   => true,
                            'all_descendants' => true
                        )
                    );
                }

                $permission = array(
                    'read_data' => ($read ? true : false),
                    'write_data' => ($write ? true : false),
                    'exe_file' => ($read ? true : false),
                    'append_data' => ($write ? true : false),
                    'delete' => ($write ? true : false),
                    'delete_sub' => ($write ? true : false),
                    'read_attr' => ($read ? true : false),
                    'write_attr' => ($write ? true : false),
                    'read_ext_attr' => ($read ? true : false),
                    'write_ext_attr' => ($write ? true : false),
                    'read_perm' => ($read ? true : false),
                    'change_perm' => false,
                    'take_ownership' => false
                    );

                $rules[] = array(
                    'owner_type'        => 'user',
                    'owner_name'        => $username,
                    'permission_type'   => 'allow',
                    'permission'        => $permission,
                    'inherit'           => array(
                            'child_files'   => true,
                            'child_folders' => true,
                            'this_folder'   => true,
                            'all_descendants' => true
                        )
                    );

                if( $this->_syno->setFileShareACLs('/volume1', $usersharename . $dir, $rules) ) {
                    return true;
                }
            }

            return false;
        }
    }