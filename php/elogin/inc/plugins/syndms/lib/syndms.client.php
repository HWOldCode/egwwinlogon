<?php

    require_once('syndms.request.php');

    /**
     * Description of syndms
     *
     * https://global.download.synology.com/ftp/Document/DeveloperGuide/Synology_File_Station_API_Guide.pdf
     * @author Stefan Werfling
     */
    class SyndmsClient {

        const URL_INDEX = 'webman/index.cgi';
        const URL_QUERY = 'webapi/query.cgi';
        const URL_AUTH  = 'webapi/auth.cgi';

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
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.Core.User', array(
                    'method'            => 'create',
                    'version'           => '1',
                    'name'              => $name,
                    'passowrd'          => $password,
                    'description'       => $description,
                    'email'             => $email,
                    'cannot_chg_passwd' => ($cannot_chg_passwd ? 'true' : 'false'),
                    'expired'           => $expired,
                    'notify_by_email'   => 'false',
                    'send_password'     => 'false',
                    ));

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
    }
