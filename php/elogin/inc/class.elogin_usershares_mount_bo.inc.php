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
         * getUsershare
         *
         * @return elogin_usershares_bo
         */
        public function getUsershare() {
            return new elogin_usershares_bo($this->_usershare_id);
        }

        /**
         * setMachineId
         *
         * @param string $id
         */
        public function setMachineId($id) {
            $this->_machine_id = $id;
        }

        /**
         * getMachineId
         *
         * @return string
         */
        public function getMachineId() {
            return $this->_machine_id;
        }

        /**
         * setShareSource
         *
         * @param string $sharesource
         */
        public function setShareSource($sharesource) {
            $this->_share_source = $sharesource;
        }

        /**
         * getShareSource
         *
         * @return string
         */
        public function getShareSource() {
            return $this->_share_source;
        }

        /**
         * setMountname
         *
         * @param string $name
         */
        public function setMountname($name) {
            $this->_mount_name = $name;
        }

        /**
         * getMountname
         *
         * @return string
         */
        public function getMountname() {
            return $this->_mount_name;
        }

        /**
         * getCmds
         *
         * @param type $system
         * @return elogin_cmd_bo|null
         */
        public function getCmd($system=null) {
            if( $system == null ) {
                $system = elogin_bo::SYSTEM_WIN;
            }

            $replace_str = null;

            switch( $system ) {
                case elogin_bo::SYSTEM_WIN:
                    $replace_str = 'net use <drivename>: "\\\\<server>\<share>" /user:<username> <password>';
                    break;
            }

            if( $replace_str ) {
                $us             = $this->getUsershare();
                $username       = $us->getUsername();
                $sharepassword  = $us->getSharePassword();

                if( $this->getMountname() != '' ) {
                    $cmd = $replace_str;
                    $cmd = str_replace('<drivename>', $this->getMountname(), $cmd);
                    $cmd = str_replace('<server>', $us->getProvider()->getMountAddress(), $cmd);
                    $cmd = str_replace('<share>', $this->getShareSource(), $cmd);
                    $cmd = str_replace('<username>', $username, $cmd);
                    $cmd = str_replace('<password>', $sharepassword, $cmd);

                    $ecmd = new elogin_cmd_bo('');
                    $ecmd->setAccountId($us->getUserId());

                    $mid = $this->getMachineId();

                    $ecmd->setMachineId(($mid == null ? 'all' : $mid));
                    $ecmd->setCommand($cmd);
                    $ecmd->setSystem($system);

                    return $ecmd;
                }
            }

            return null;
        }

        /**
         * save
         */
        public function save() {
            $data = array();

            if( $this->_id ) {
                $data['el_unid'] = $this->_id;
            }

            $data['el_usershare_id']        = $this->_usershare_id;
            $data['el_machine_id']          = $this->_machine_id;
            $data['el_account_id']          = $this->_account_id;
            $data['el_share_source']        = $this->_share_source;
            $data['el_mount_name']          = $this->_mount_name;

            $return = self::_write($data);

            if( $return ) {
                if( !($this->_id) ) {
                    $this->_id = $return;
                }
            }
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
            $join = '';

            if (!($data = self::$_db->select(self::TABLE, $cols, $where, __LINE__, __FILE__,
                false, '', false, -1, $join)->fetch()))
            {
                return false;
            }

            return $data;
        }

        /**
         * _write
         *
         * @param array $data
         */
        static protected function _write(array $data) {
            if( isset($data['el_unid']) ) {
                $unid = $data['el_unid'];
                unset($data['el_unid']);

                self::$_db->update(
                    self::TABLE,
                    $data,
                    array(
                        'el_unid' => $unid,
                        ),
                    __LINE__,
                    __FILE__,
                    'elogin'
                    );
            }
            else {
                $data['el_unid'] = elogin_bo::getPHPUuid();

                self::$_db->insert(
                    self::TABLE,
                    $data,
                    false,
                    __LINE__,
                    __FILE__,
                    'elogin'
                    );
            }

            return $data['el_unid'];
        }

        /**
         * get_rows
         *
         * @param type $query
         * @param type $rows
         * @param type $readonlys
         * @return type
         */
        static public function get_rows(&$query, &$rows, &$readonlys) {
            $where = array();
            $cols = array(self::TABLE . '.*');
            $join = '';

            if( key_exists('col_filter', $query) ) {
                if( isset($query['col_filter']['el_share_source']) ) {
                    $where['el_share_source'] = $query['col_filter']['el_share_source'];
                }

                if( isset($query['col_filter']['el_usershare_id']) ) {
                    $where['el_usershare_id'] = $query['col_filter']['el_usershare_id'];
                }
            }

            if (!($rs = self::$_db->select(self::TABLE, $cols, $where, __LINE__, __FILE__,
                false, '', false, -1, $join)))
            {
                return array();
            }

            $rows = array();

            foreach( $rs as $row ) {
				$row = (array) $row;
                $rows[] = $row;
            }

            return count($rows);
        }

        /**
         * getUserShareMountsBy
         *
         * @param string $sharename
         * @param string $usershareid
         * @return array of elogin_usershares_mount_bo
         */
        static public function getUserShareMountsBy($sharename=null, $usershareid=null) {
            $colfilter = array();

            if( $sharename ) {
                $colfilter['el_share_source'] = $sharename;
            }

            if( $usershareid ) {
                $colfilter['el_usershare_id'] = $usershareid;
            }

            $query = array(
                'col_filter' => $colfilter/*array(
                    'el_share_source' => $sharename,
                    'el_usershare_id' => $usershareid
                    )*/
                );

            $rows = array();
            $readonlys = array();

            self::get_rows($query, $rows, $readonlys);

            $list = array();

            foreach( $rows as $row ) {
                $list[] = new elogin_usershares_mount_bo($row['el_unid']);
            }

            return $list;
        }
    }

    /**
     * init
     */
    elogin_usershares_mount_bo::init_static();