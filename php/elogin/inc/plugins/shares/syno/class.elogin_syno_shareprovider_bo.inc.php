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

    require_once('lib/syndms.client.php');

    /**
     * elogin_syno_shareprovider_bo
     */
    class elogin_syno_shareprovider_bo extends elogin_shareprovider_bo {

		// consts
		const PROVIDER_NAME = 'Synology DSM';

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
         * __construct
         * @param string $id
         */
        public function __construct($id=null) {}

        /**
         * __destruct
         */
        public function __destruct() {
            if( $this->_syno != null ) {
                if( $this->_syno->isLogin() ) {
                    //$this->_syno->logout();
                }
            }
        }

        /**
         * logout
         * @return boolean
         */
        public function logout() {
            if( $this->_syno != null ) {
                if( $this->_syno->isLogin() ) {
                    $this->_syno->logout();

                    return true;
                }
            }

            return false;
        }

        /**
         * login
         * @return boolean
         */
        public function login() {
            if( !$this->_syno->isLogin() ) {
				if( $this->isActiv() ) {
					if( $this->_syno->login($this->_account_user, $this->_account_password) ) {
						return true;
					}
				}
            }
			else {
				return true;
			}

            return false;
        }

        /**
         * _construct2
         */
        protected function _construct2() {
            if( $this->_account_server != null ) {
                if( isset(elogin_syno_shareprovider_bo::$_synoInstances[$this->_id]) ) {
                    $this->_syno = elogin_syno_shareprovider_bo::$_synoInstances[$this->_id];
                }
                else {
                    $this->_syno = new SyndmsClient(
                        $this->_account_server,
                        $this->_account_port,
						$this->_protocol,
						$this->_api_version
                        );

                    elogin_syno_shareprovider_bo::$_synoInstances[$this->_id] = $this->_syno;
                }


                $this->login();
            }
        }

		/**
		 * getInstanceProviderName
		 * @return string
		 */
		public function getInstanceProviderName() {
			return self::PROVIDER_NAME;
		}

		/**
		 * getProtocolNames
		 * @return array|null
		 */
		public function getProtocolNames() {
			return array(
				'http' => 'Http',
				'https' => 'Https'
			);
		}

		/**
		 * getApiVersions
		 * @return array|null
		 */
		public function getApiVersions() {
			return array(
				SyndmsClient::VERSION_DSM_5 => 'DSM 5.* =< ',
				SyndmsClient::VERSION_DSM_6 => 'DSM 6.* =< '
				);
		}

        /**
         * getShares
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
         * @param int|elogin_usershares_bo $account
         */
        public function createUserShares($account) {
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
         * @param string $usersharename
         * @param string $dir
		 * @param int $limit
         * @return array
         */
        public function getShareDirList($usersharename, $dir="", $limit=1000) {
            if( $this->_syno ) {
				$dir = trim($dir);

				if( $dir != '' ) {
					$tdir = str_replace("\\", "/", $dir);

					if( $tdir[0] == "/" ) {
						$tdir = substr($tdir, 1);
					}

					$tdirs = explode("/", $tdir);

					$dirname = $tdirs[count($tdirs)-1];

					if( trim($dirname) == '' ) {
						$dirname = $tdirs[count($tdirs)-2];
						unset($tdirs[count($tdirs)-2]);
					}

					unset($tdirs[count($tdirs)-1]);

					$dirpath = implode($tdirs, "/");

					$usersharename = $usersharename . "/" . $dirpath . "/" . $dirname;
					$usersharename = str_replace("//", "/", $usersharename);
				}


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
         * @param string $usersharename
         * @param string $dir
         * @return boolean
         */
        public function existShareDir($usersharename, $dir) {
            if( $this->_syno ) {
                $list = $this->getShareDirList($usersharename, $dir, 1);

                if( is_array($list) ) {
                    return true;
                }
            }

            return false;
        }

        /**
         * createShareDir
         * @param string $usersharename
         * @param string $dir
         * @return boolean
         */
        public function createShareDir($usersharename, $dir) {
            if( $this->_syno ) {
				$dir = trim($dir);

				if( $dir != '' ) {
					$tdir = str_replace("\\", "/", $dir);

					if( $tdir[0] == "/" ) {
						$tdir = substr($tdir, 1);
					}

					$tdirs = explode("/", $tdir);

					$dirname = $tdirs[count($tdirs)-1];

					if( trim($dirname) == '' ) {
						$dirname = $tdirs[count($tdirs)-2];

						unset($tdirs[count($tdirs)-2]);
					}

					unset($tdirs[count($tdirs)-1]);

					$dirpath = implode($tdirs, "/");

					$usersharename = $usersharename . "/" . $dirpath;
					$usersharename = str_replace("//", "/", $usersharename);

					if( $usersharename[strlen($usersharename)-1] == '/' ) {
						$usersharename = substr($usersharename, 0, strlen($usersharename)-1);
					}
				}

                if( $this->_syno->createDirShare($usersharename, $dirname) ) {
                    return true;
                }
            }

            return false;
        }

        /**
         * removeAllPermissionDir
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
					error_log("addPermissionDir user not found: '" . $username . "'");
                    return false;
                }

				$dir = str_replace("//", "/", $dir);

                $list = $this->_syno->getFileShareACLs(
					'/volume1' . $usersharename . $dir);

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

				error_log("setFileShareACLs not set: '" . $username . "'");
            }

            return false;
        }

		/**
         * addPermissionDirMulti
         * @param string $usersharename
         * @param string $dir
         * @param array $usernames
         * @param boolean $read
         * @param boolean $write
		 * @param boolean $ignorUser
         * @return boolean
         */
        public function addPermissionDirMulti($usersharename, $dir, $usernames, $read=false, $write=false, $ignorUser=true) {
			if( $this->_syno ) {
				if( !is_array($usernames) ) {
					return false;
				}

				$userlist = array();

				foreach( $usernames as $username ) {
					if( !$this->isUsernameExist($username) ) {
						if( $ignorUser ) {
							continue;
						}

						error_log("addPermissionDir user not found: '" . $username . "'");
						return false;
					}
					else {
						$userlist[] = $username;
					}
				}

				$dir = str_replace("//", "/", $dir);

				// -------------------------------------------------------------

				$list = $this->_syno->getFileShareACLs(
					'/volume1' . $usersharename . $dir);

                $rules = array();

                foreach( $list as $tusername => $permission ) {
                    if( in_array($tusername, $userlist)  ) {
                        continue;
                    }

                    $rules[] = array(
                        'owner_type'			=> 'user',
                        'owner_name'			=> $tusername,
                        'permission_type'		=> 'allow',
                        'permission'			=> $permission,
                        'inherit'				=> array(
                            'child_files'		=> true,
                            'child_folders'		=> true,
                            'this_folder'		=> true,
                            'all_descendants'	=> true
                        )
                    );
                }

				$permission = array(
                    'read_data'			=> ($read ? true : false),
                    'write_data'		=> ($write ? true : false),
                    'exe_file'			=> ($read ? true : false),
                    'append_data'		=> ($write ? true : false),
                    'delete'			=> ($write ? true : false),
                    'delete_sub'		=> ($write ? true : false),
                    'read_attr'			=> ($read ? true : false),
                    'write_attr'		=> ($write ? true : false),
                    'read_ext_attr'		=> ($read ? true : false),
                    'write_ext_attr'	=> ($write ? true : false),
                    'read_perm'			=> ($read ? true : false),
                    'change_perm'		=> false,
                    'take_ownership'	=> false
                    );

				foreach( $userlist as $username ) {
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
				}

				if( $this->_syno->setFileShareACLs('/volume1', $usersharename . $dir, $rules) ) {
					return true;
				}


				error_log("setFileShareACLs not set: '" . var_export($userlist, true) . "'");
			}

            return false;
        }

		/**
		 * getDeviceInfo
		 * @return array
		 */
		public function getDeviceInfo($update=false) {
			if( ($this->_device_info == '') ||
				(is_array($this->_device_info) && (count($this->_device_info) == 0)) )
			{
				$update = true;
			}

			if( $update ) {
				if( $this->_syno ) {
					$infos = $this->_syno->getStorageInfo();

					if( is_array($infos) ) {
						$this->_device_info = $infos;
					}
				}
			}

			return $this->_device_info;
		}

		/**
		 * getDeviceSizeTotal
		 * @param boolean $update
		 * @return boolean
		 */
		public function getDeviceSizeTotal($update=false) {
			$infos = $this->getDeviceInfo($update);

			if( is_array($infos) && isset($infos['volumes']) ) {
				if( is_array($infos['volumes']) ) {
					$size = 0;

					foreach( $infos['volumes'] as $volume ) {
						if( isset($volume['size']) ) {
							$size += $volume['size']['total'];
						}
					}

					return $size;
				}
			}

			return false;
		}

		/**
		 * getDeviceSizeUsed
		 * @param boolean $update
		 * @return boolean
		 */
		public function getDeviceSizeUsed($update=false) {
			$infos = $this->getDeviceInfo($update);

			if( is_array($infos) && isset($infos['volumes']) ) {
				if( is_array($infos['volumes']) ) {
					$size = 0;

					foreach( $infos['volumes'] as $volume ) {
						if( isset($volume['size']) ) {
							$size += $volume['size']['used'];
						}
					}

					return $size;
				}
			}

			return false;
		}

		/**
		 * setUseCacheLogging
		 * @param boolean $logging
		 */
		public function setUseCacheLogging($logging=false) {
			SyndmsRequest::setCacheLogging($logging);
		}

		/**
		 * getCacheLogs
		 * @return array
		 */
		public function getCacheLogs() {
			return array(
				'requests' => SyndmsRequest::getCacheLogs(),
				);
		}
    }