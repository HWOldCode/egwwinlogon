<?php

    require_once('syndms.request.php');

    /**
     * Description of syndms
     *
     * https://global.download.synology.com/ftp/Document/DeveloperGuide/Synology_File_Station_API_Guide.pdf
     * @author Stefan Werfling
     */
    class SyndmsClient {

        const URL_INDEX         = 'webman/index.cgi';
        const URL_QUERY         = 'webapi/query.cgi';
        const URL_AUTH          = 'webapi/auth.cgi';
        const URL_FILESHARE     = 'webapi/FileStation/file_share.cgi';

        const SYNO_SDS_SESSISON = 'SYNO.SDS.Session';

        /**
         * IP
         * @var string
         */
        protected $_ip = '';

        /**
         * Port
         * @var int
         */
        protected $_port = 5000;

        /**
         * SDS Session
         * @var array
         */
        protected $_sds_session = null;

        /**
         * cookies
         * @var array
         */
        protected $_cookies = array();

        /**
         *
         * @var type
         */
        protected $_isLogin = false;

        /**
         * services
         * @var array
         */
        protected $_services = array();

        /**
         * __construct
         *
         * @param string $ip
         * @param int $port
         */
        public function __construct($ip, $port=5000) {
            $this->_ip      = $ip;
            $this->_port    = $port;
        }

        protected function _createUrl($url) {
            return 'http://' . $this->_ip . ':' . $this->_port . '/' . $url;
        }

        /**
         * _request
         *
         * @param string $url
         * @param array $data
         * @return array
         */
        protected function _request($url, $data=null) {
            $url = $this->_createUrl($url);

            $header = array();

            if( count($this->_cookies) > 0 ) {
                $cookie = "";

                foreach( $this->_cookies as $name => $value ) {
                    if( $cookie != "" ) {
                        $cookie .= " ";
                    }

                    $cookie .= $name . "=" . $value . ";";
                }

                $header[] = 'Cookie: ' . $cookie;
            }

            $response = SyndmsRequest::curlRequest($url, $data, null, $header);

            if( $response ) {
                $cookies = array();

                preg_match_all('/Set-Cookie:(?<cookie>\s{0,}.*)$/im', $response['header'], $cookies);

                if( is_array($cookies) && isset($cookies['cookie']) ) {
                    foreach( $cookies['cookie'] as $cstr ) {
                        $tmp = explode(";", $cstr);

                        list($fieldname, $value) = explode('=', $tmp[0]);

                        $this->_cookies[$fieldname] = $value;
                    }
                }
            }

            return $response;
        }

        /**
         * _queryByService
         *
         * @param string $serviceName
         * @param array $query
         * @return array|null
         */
        protected function _queryByService($serviceName, $query) {
            if( isset($this->_services[$serviceName]) ) {
                $service = $this->_services[$serviceName];

                if( !is_array($query) ) {
                    $query = array();
                }

                $query['api'] = $serviceName;

                $response = $this->_request(
                    'webapi/' . $service['path'],
                    $query);

                if( $service['requestFormat'] == 'JSON' ) {
                    $data = json_decode($response['body']);

                    if( $data ) {
                        $data = (array) $data;

                        if( isset($data['success']) && ($data['success']) ) {
                            if( isset($data['data']) ) {
                                $rdata = (array) $data['data'];

                                if( count($rdata) == 0 ) {
                                    return $data['success'];
                                }

                                return $rdata;
                            }
                            else {
                                return $data['success'];
                            }
                        }
                    }
                }
                else {
                    return $response;
                }
            }

            return null;
        }

        /**
         * _query
         *
         * @param array $query
         * @return array
         */
        protected function _query($query) {
            $response = $this->_request(self::URL_QUERY, $query);

            if( $response ) {
                $data = json_decode($response['body']);

                if( $data instanceof stdClass ) {
                    return (array) $data;
                }
            }

            return null;
        }

        /**
         * _initConnection
         * @return boolean
         */
        public function _initConnection() {
            $response = $this->_request(self::URL_INDEX);

            $body = $response['body'];

            if( ($bpos = strpos($body, self::SYNO_SDS_SESSISON)) !== false ) {
                $bpos = strpos($body, "{", $bpos);
                $epos = strpos($body, ";", $bpos);

                $bspos = $bpos-1;

                $strsessiondata = substr(
                    $body,
                    $bspos,
                    $epos-$bspos
                    );

                $sessiondata =  json_decode($strsessiondata);

                if( $sessiondata instanceof stdClass ) {
                    $this->_sds_session = (array) $sessiondata;
                    return true;
                }
            }

            return false;
        }

        /**
         * _initServices
         */
        public function _initServices() {
            if( $this->_sds_session ) {
                $this->_services = array();

                $query = array(
                    'query'     => 'all',
                    'api'       => 'SYNO.API.Info',
                    'method'    => 'query',
                    'version'   => '1'
                    );

                $response = $this->_query($query);

                if( $response && is_array($response) && isset($response['data']) ) {
                    $data = $response['data'];

                    if( $data ) {
                        $data = (array) $data;

                        foreach( $data as $servicename => $tservice ) {
                            $this->_services[$servicename] = (array) $tservice;
                        }
                    }
                }
            }
        }

        /**
         * login
         *
         * @param string $username
         * @param string $password
         * @return boolean
         */
        public function login($username, $password) {
            if( $this->_initConnection() ) {
                $this->_initServices(); // TODO

                $response = $this->_request(self::URL_AUTH, array(
                    'api' => 'SYNO.API.Auth',
                    'version' => '3',
                    'method' => 'login',
                    'account' => $username,
                    'passwd' => $password,
                    'session' => 'FileStation',
                    'format' => 'cookie'));

                if( $response ) {
                    $data = json_decode($response['body']);

                    if( $data ) {
                        $data = (array) $data;

                        if( isset($data['success']) && ($data['success']) ) {
                            $this->_isLogin = true;
                            return true;
                        }
                    }
                }
            }

            $this->_isLogin = false;
            return false;
        }

        /**
         * isLogin
         * @return boolean
         */
        public function isLogin() {
            return $this->_isLogin;
        }

        /**
         * getUsers
         *
         * @return array|null
         */
        public function getUsers() {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.Core.User', array(
                        'action'    => 'list',
                        'type'      => 'local',
                        'offset'    => 0,
                        'limit'     => 50,
                        'additional' => '["email","description","expired"]',
                        'method'    => 'list',
                        'version'   => '1'
                    ));

                if( $data ) {
                    if( isset($data['users']) ) {
                        $users = array();

                        foreach( $data['users'] as $tuser ) {
                            $tuser = (array)$tuser;
                            $users[$tuser['uid']] = $tuser;
                        }

                        return $users;
                    }
                }
            }

            return null;
        }

        /**
         * getUserGroups
         *
         * @param string $username
         * @return array|null
         */
        public function getUserGroups($username) {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.Core.Group', array(
                        'name_only' => 'false',
                        'user'      => $username,
                        'type'      => 'local',
                        'method'    => 'list',
                        'version'   => '1'
                    ));

                if( $data ) {
                    if( isset($data['groups']) ) {
                        $groups = array();

                        foreach( $data['groups'] as $tgroup ) {
                            $tgroup = (array) $tgroup;

                            $groups[$tgroup['gid']] = $tgroup;
                        }

                        return $groups;
                    }
                }
            }

            return null;
        }

        /**
         * getGroups
         *
         * @return array|null
         */
        public function getGroups() {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.Core.Group', array(
                        'name_only' => 'false',
                        'action'    => 'enum',
                        'offset'    => 0,
                        'limit'     => 50,
                        'type'      => 'local',
                        'method'    => 'list',
                        'version'   => '1'
                    ));

                if( $data ) {
                    if( isset($data['groups']) ) {
                        $groups = array();

                        foreach( $data['groups'] as $tgroup ) {
                            $tgroup = (array) $tgroup;

                            $groups[$tgroup['gid']] = $tgroup;
                        }

                        return $groups;
                    }
                }
            }

            return null;
        }

        /**
         * getShares
         *
         * @return array|null
         */
        public function getShares() {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.Core.Share', array(
                        'additional' => '["hidden","encryption","is_aclmode","migrate","unite_permission","is_support_acl","is_sync_share"]',
                        'action'    => 'enum',
                        'shareType' => 'all',
                        'offset'    => 0,
                        'limit'     => 50,
                        'type'      => 'local',
                        'method'    => 'list',
                        'version'   => '1'
                    ));

                if( $data ) {
                    if( isset($data['shares']) ) {
                        $shares = array();

                        foreach( $data['shares'] as $tshare ) {
                            $tshare = (array) $tshare;
                            $shares[] = $tshare;
                        }

                        return $shares;
                    }
                }
            }

            return null;
        }

        /**
         * createUser
         *
         * @param string $name
         * @param string $password
         * @param boolean $cannot_chg_passwd
         * @param string $expired
         * @param string $description
         * @param string $email
         * @return boolean|string
         */
        public function createUser($name, $password, $cannot_chg_passwd=false, $expired='normal', $description='', $email='') {
        //var_dump($name);
        //var_dump($this->_isLogin);
            if( $this->_isLogin ) {
                $query = array(
                    'method'            => 'create',
                    'version'           => '1',
                    'name'              => $name,
                    'password'          => $password,
                    'description'       => $description,
                    'email'             => $email,
                    'cannot_chg_passwd' => ($cannot_chg_passwd ? 'true' : 'false'),
                    'expired'           => $expired,
                    'notify_by_email'   => 'false',
                    'send_password'     => 'false',
                    );

            //var_dump($query);
                $data = $this->_queryByService('SYNO.Core.User', $query);
            //var_dump($data);
                if( $data ) {
                    if( isset($data['name']) && isset($data['uid']) ) {
                        return $data['uid'];
                    }
                }
            }

            return false;
        }

        /**
         * addUserToGroup
         *
         * @param string $groupname
         * @param string $username
         * @return boolean
         */
        public function addUserToGroup($groupname, $username) {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.Core.Group.Member', array(
                    'method'            => 'add',
                    'version'           => '1',
                    'name'              => $username,
                    'group'             => $groupname
                    ));

                if( $data ) {
                    return true;
                }
            }

            return false;
        }

        /**
         * removeUserByGroup
         *
         * @param string $groupname
         * @param string $username
         * @return boolean
         */
        public function removeUserByGroup($groupname, $username) {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.Core.Group.Member', array(
                    'method'            => 'remove',
                    'version'           => '1',
                    'name'              => $username,
                    'group'             => $groupname
                    ));

                if( $data ) {
                    return true;
                }
            }

            return false;
        }

        /**
         * createShare
         *
         * @param string $name
         * @param string $vol_path
         * @param string $desc
         * @param boolean $hide_unreadable
         * @return boolean
         */
        public function createShare($name, $vol_path, $desc='', $hide_unreadable=true) {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.Core.Share', array(
                    'method'            => 'create',
                    'version'           => '1',
                    'name'              => $name,
                    'shareinfo'         => json_encode(array(
                        'name'              => $name,
                        'vol_path'          => $vol_path,
                        'desc'              => $desc,
                        'hide_unreadable'   => $hide_unreadable,
                        'name_org'          => ''
                        ))
                    ));

                if( $data ) {
                    return true;
                }
            }

            return false;
        }

        /**
         * getUser
         *
         * @param string $username
         * @return boolean
         */
        public function getUser($username) {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.Core.User', array(
                    'method'            => 'get',
                    'version'           => '1',
                    'name'              => $username,
                    'additional'        => '["description","email","expired","cannot_chg_passwd"]'
                    ));

                if( $data ) {
                    if( isset($data['users']) ) {
                        $users = array();

                        foreach( $data['users'] as $tuser ) {
                            return (array)$tuser;
                        }
                    }
                }
            }

            return false;
        }

        /**
         * setUser
         *
         * @param type $username
         * @param type $newpassword
         * @param type $newusername
         * @param type $description
         * @param type $email
         * @param type $cannot_chg_passwd
         * @param type $expired
         * @return boolean
         */
        public function setUser($username, $newpassword=null, $newusername=null, $description, $email, $cannot_chg_passwd=false, $expired='normal') {
            if( $this->_isLogin ) {
                $query = array(
                    'method'            => 'set',
                    'version'           => '1',
                    'name'              => $username,
                    'description'       => $description,
                    'email'             => $email,
                    'cannot_chg_passwd' => ($cannot_chg_passwd ? 'true' : 'false'),
                    'expired'           => $expired,
                    'new_name'          => ($newusername != null ? $newusername : $username )
                    );

                if( $newpassword ) {
                    $query['password'] = $newpassword;
                }

                $data = $this->_queryByService('SYNO.Core.User', $query);

                if( $data ) {
                    if( isset($data['name']) && isset($data['uid']) ) {
                        return $data['uid'];
                    }
                }
            }

            return false;
        }

        /**
         * setUserPassword
         *
         * @param string $username
         * @param string $newpassword
         * @return boolean
         */
        public function setUserPassword($username, $newpassword) {
            if( $this->_isLogin ) {
                $userdata = $this->getUser($username);

                if( is_array($userdata) ) {
                    $treturn = $this->setUser(
                        $username,
                        $newpassword,
                        null,
                        $userdata['description'],
                        $userdata['email'],
                        false,
                        $userdata['expired']
                        );

                    if( $treturn !== false ) {
                        return true;
                    }
                }
            }

            return false;
        }

        /**
         * disableUser
         *
         * @param string $username
         * @return boolean
         */
        public function disableUser($username) {
            if( $this->_isLogin ) {
                $userdata = $this->getUser($username);

                if( is_array($userdata) ) {
                    $treturn = $this->setUser(
                        $username,
                        null,
                        null,
                        $userdata['description'],
                        $userdata['email'],
                        false,
                        'now'
                        );

                    if( $treturn !== false ) {
                        return true;
                    }
                }
            }

            return false;
        }

        /**
         * enableUser
         *
         * @param string $username
         * @return boolean
         */
        public function enableUser($username) {
            if( $this->_isLogin ) {
                $userdata = $this->getUser($username);

                if( is_array($userdata) ) {
                    $treturn = $this->setUser(
                        $username,
                        null,
                        null,
                        $userdata['description'],
                        $userdata['email'],
                        false,
                        'normal'
                        );

                    if( $treturn !== false ) {
                        return true;
                    }
                }
            }

            return false;
        }

        /**
         * isUserDisabled
         *
         * @param string $username
         * @return boolean
         */
        public function isUserDisabled($username) {
            if( $this->_isLogin ) {
                $userdata = $this->getUser($username);

                if( is_array($userdata) ) {
                    if( $userdata['expired'] == 'now' ) {
                        return true;
                    }
                }
            }

            return false;
        }

        /**
         * setSharePermission
         * @param string $sharename
         * @param string $forUsername
         * @param string $access
         * @param string $user_group_type
         * @return boolean
         */
        public function setSharePermission($sharename, $forUsername, $access='r', $user_group_type='local_user') {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.Core.Share.Permission', array(
                    'method'            => 'set',
                    'version'           => '1',
                    'name'              => $sharename,
                    'user_group_type'   => $user_group_type,
                    'permissions'       => json_encode(array(
                        array(
                            "name"          => $forUsername,
                            "is_readonly"   => ( $access == 'r' ? true : false),
                            "is_writable"   => ( $access == 'rw' ? true : false),
                            "is_deny"       => ( $access == 'd' ? true : false),
                            "is_custom"     => false
                            )
                        ))
                    ));

                if( $data ) {
                    return true;
                }
            }

            return false;
        }

        /**
         * getUserShares
         *
         * @param string $username
         * @param string $user_group_type
         * @return boolean|array
         */
        public function getUserShares($username, $user_group_type='local_user') {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.Core.Share.Permission', array(
                    'method'            => 'list_by_user',
                    'version'           => '1',
                    'name'              => $username,
                    'user_group_type'   => $user_group_type,
                    'share_type'        => '["dec","local","usb","sata"]',
                    'additional'        => '["hidden","encryption","is_aclmode"]'
                    ));

                if( $data ) {
                    if( isset($data['shares']) ) {
                        $shares = array();

                        foreach( $data['shares'] as $tshare ) {
                            $tshare = (array) $tshare;
                            $shares[] = $tshare;
                        }

                        return $shares;
                    }
                }
            }

            return false;
        }

        public function getFileSharesList($sharename, $limit=1000, $options=array()) {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.FileStation.List', array(
                    'method'            => 'list',
                    'version'           => '1',
                    'folder_path'       => $sharename,
                    'filetype'          => 'all',
                    'additional'        => 'real_path,size,owner,time,perm,type,mount_point_type',
                    'action'            => 'list',
                    'sort_direction'    => 'ASC',
                    'sort_by'           => 'name',
                    'sort_by'           => 0,
                    'limit'             => $limit,
                    ));

                var_dump($data);
            }

            return array();
        }
    }
