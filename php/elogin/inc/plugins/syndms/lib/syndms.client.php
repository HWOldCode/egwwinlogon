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

                $query = array(
                    'query'     => 'all',
                    'api'       => 'SYNO.API.Info',
                    'method'    => 'query',
                    'version'   => '1'
                    );

                $response = $this->_query($query);

                //var_dump($response);
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
                $response = $this->_request(
                    'webapi/_______________________________________________________entry.cgi',
                    array(
                        'action'    => 'list',
                        'type'      => 'local',
                        'offset'    => 0,
                        'limit'     => 50,
                        'additional' => '["email","description","expired"]',
                        'api'       => 'SYNO.Core.User',
                        'method'    => 'list',
                        'version'   => '1'
                    ));

                
            }
        }
    }
