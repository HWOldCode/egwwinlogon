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
     * elogin_usershares_mount_bo
     */
    class elogin_usershares_mount_bo {

        /**
         * TABLE
         */
        const TABLE = 'egw_elogin_usershares_mount';

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
         * user share id
         * @var string
         */
        protected $_usershare_id = null;

        /**
         * machine id
         * @var string
         */
        protected $_machine_id = null;

        /**
         * account id
         * @var string
         */
        protected $_account_id = null;

        /**
         * share source (path to share)
         * @var string
         */
        protected $_share_source = '';

        /**
         * name of mount
         * @var string
         */
        protected $_mount_name = '';

        /**
         * Init our static properties
         */
        static public function init_static() {
            self::$_db = $GLOBALS['egw']->db;
        }
        
        /**
         * constructor
         *
         * @param string $id
         */
        public function __construct($id=null) {
            if( $id != null ) {
                $data = self::read($id);
                
                if( $data ) {
                    $this->_usershare_id    = $data['el_usershare_id'];
                    $this->_machine_id      = $data['el_machine_id'];
                    $this->_account_id      = $data['el_account_id'];
                    $this->_share_source    = $data['el_share_source'];
                    $this->_mount_name      = $data['el_mount_name'];
                }
            }
            
            $this->_id = $id;
        }

        /**
         * getId
         *
         * @return string
         */
        public function getId() {
            return $this->_id;
        }

        /**
         * isGlobal
         *
         * @return boolean
         */
        public function isGlobal() {
            if( ($this->_machine_id == null) || ($this->_account_id == null) ) {
                return true;
            }

            return false;
        }

        /**
         * getUsershareId
         *
         * @return string
         */
        public function getUsershareId() {
            return $this->_usershare_id;
        }

        /**
         * setUsershareId
         *
         * @param string $id
         */
        public function setUsershareId($id) {
            $this->_usershare_id = $id;
        }
        
        /**
         * read
         *
         * @param string $id
         * @return boolean|array
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
     * init
     */
    elogin_usershares_mount_bo::init_static();