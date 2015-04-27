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
     * elogin_shareprovider_bo
     */
    class elogin_shareprovider_bo {

        /**
         * TABLE
         */
        const TABLE = 'egw_elogin_shareproviders';

        /**
         * Reference to global db object
         *
         * @var egw_db
         */
        static protected $_db;

        /**
         * id
         * @var string
         */
        protected $_id = null;

        /**
         * provider name
         * @var string
         */
        protected $_provider_name = '';

        /**
         * username
         * @var string
         */
        protected $_username = null;

        /**
         * account server
         * @var string
         */
        protected $_account_server = '';

        /**
         * account port
         * @var int
         */
        protected $_account_port = 0;

        /**
         * account user
         * @var string
         */
        protected $_account_user = '';

        /**
         * account password
         * @var string
         */
        protected $_account_password = '';

        /**
         * Init our static properties
         */
        static public function init_static() {
            self::$_db = $GLOBALS['egw']->db;
        }

        /**
         * i
         * return a instance by db
         *
         * @param string $id
         * @return elogin_shareprovider_bo
         */
        static public function i($id) {
            $provider = new elogin_shareprovider_bo($id);
            return $provider->_cast();
        }

        /**
         * constructor
         */
        public function __construct($id) {
            $this->_id = $id;

            $data = self::read($id);

            if( $data ) {
                $this->_provider_name       = $data['el_provider_name'];
                $this->_account_server      = $data['el_account_server'];
                $this->_account_port        = intval($data['el_account_port']);
                $this->_account_user        = $data['el_account_user'];
                $this->_account_password    = $data['el_account_password'];
            }
        }

        /**
         * _initcast
         *
         * @return elogin_shareprovider_bo
         */
        private function _cast() {
            $provider = elogin_shareprovider_bo::getShareProviderByName($this->_provider_name);

            if( $provider instanceof elogin_shareprovider_bo ) {
                $provider->_id                  = $this->_id;
                $provider->_provider_name       = $this->_provider_name;
                $provider->_account_server      = $this->_account_server;
                $provider->_account_port        = $this->_account_port;
                $provider->_account_user        = $this->_account_user;
                $provider->_account_password    = $this->_account_password;
                $provider->_username            = $this->_username;

                return $provider;
            }

            return null;
        }

        /**
         * setAccount
         *
         * @param string $server
         * @param int $port
         * @param string $user
         * @param string $password
         */
        public function setAccount($server, $port, $user, $password) {
            $this->_account_server = $server;
            $this->_account_port = $port;
            $this->_account_user = $user;
            $this->_account_password = $password;
        }

        /**
         * setUsername
         *
         * @param string $username
         */
        public function setUsername($username) {
            $this->_username = $username;
        }

        /**
         * getProviderName
         * 
         * @return string
         */
        public function getProviderName() {
            return $this->_provider_name;
        }

        /**
         * getShares
         *
         * @return array
         */
        public function getShares() {
            return array();
        }

        /**
         * getShareProviderByName
         *
         * @param string $name
         * @return elogin_shareprovider_bo
         */
        static public function getShareProviderByName($name) {
            $classname  = 'elogin_' . strtolower($name) . '_shareprovider_bo';
            $tclass     = null;

            if( class_exists($classname) ) {
                $tclass = new $classname();
            }

            $lname = strtolower($name);
            $inc = __DIR__ . '/plugins/shares/' . $lname .
                '/class.elogin_' . $lname . '_shareprovider_bo.inc.php';

            if( file_exists($inc) ) {
                require_once($inc);

                if( class_exists($classname) ) {
                    $tclass = new $classname();
                }
            }

            if( $tclass instanceof elogin_shareprovider_bo ) {
                return $tclass;
            }

            return null;
        }

        /**
         * read
         *
         * @param string $id
         * @return boolean
         */
        static public function read($id=null) {
            $where = array(self::TABLE . '.el_unid=' . "'" . (string)$id . "'");
            $cols = array(self::TABLE . '.*');
            $join = array();

            if (!($data = self::$_db->select(self::TABLE, $cols, $where, __LINE__, __FILE__,
                '', '', 0, $join)->fetch()))
            {
                return false;
            }

            return $data;
        }
    }


    /**
     * elogin_shareprovider_bo
     */
    elogin_shareprovider_bo::init_static();