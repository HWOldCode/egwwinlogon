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
     * elogin_usershares_bo
     */
    class elogin_usershares_bo {

        /**
         * DESTINATION_SYNO
         */
        const DESTINATION_SYNO = 'syno';

        /**
         * TABLE
         */
        const TABLE = 'egw_elogin_usershares';

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
         * provider id
         * @var string
         */
        protected $_providerid = null;

        /**
         * egroupware account id
         * @var int
         */
        protected $_egwaccountid = null;

        /**
         * sharepassword
         *
         * @var string
         */
        protected $_sharepassword = null;

        /**
         * share infos
         * @var array
         */
        protected $_shareinfos = array();

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
         * @param string $destination
         */
        public function __construct($id=null) {
            if( $id != null ) {
                $data = self::read($id);

                if( $data ) {
                    $this->_providerid = $data['el_provider_id'];
                    $this->_egwaccountid = $data['el_egw_account'];
                    $this->_sharepassword = $data['el_sharepassword'];

                    if( $data['el_shareinfo'] != '' ) {
                        $this->_shareinfos = json_decode($data['el_shareinfo']);
                    }
                }

                $this->_id = $id;
            }
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
         * setProviderId
         *
         * @param string $id
         */
        public function setProviderId($id) {
            $this->_providerid =  $id;
        }

        /**
         * getProviderId
         *
         * @return string
         */
        public function getProviderId() {
            return $this->_providerid;
        }

        /**
         * getProvider
         *
         * @return elogin_shareprovider_bo|null
         */
        public function getProvider($getRaw=false) {
            if( $getRaw ) {
                $tporvider = new elogin_shareprovider_bo($this->_providerid);

                return $tporvider;
            }

            $tporvider = elogin_shareprovider_bo::i($this->_providerid);

            if( $tporvider ) {
                $tporvider->setUsername($this->getUsername());
                return $tporvider;
            }

            return null;
        }

        /**
         * getUserId
         * @return string
         */
        public function getUserId() {
            return $this->_egwaccountid;
        }

        /**
         * getUsername
         *
         * @return string
         */
        public function getUsername() {
            return accounts::id2name($this->_egwaccountid);
        }

        /**
         * setUser
         *
         * @param int $accountid
         */
        public function setUser($accountid=null) {
            if( $accountid == null ) {
                $accountid = $GLOBALS['egw_info']['user']['account_id'];
            }

            $this->_egwaccountid = $accountid;
        }

        /**
         * getSharePassword
         *
         * @return string
         */
        public function getSharePassword() {
            return $this->_sharepassword;
        }

        /**
         * _getRandomPassword
         * @param int $len
         * @return string
         */
        protected function _getRandomPassword($len=12) {
            $alphabet       = "abcdefghijklmnopqrstuwxyzABCDEFGHIJKLMNOPQRSTUWXYZ0123456789";
            $pass           = array();
            $alphaLength    = strlen($alphabet) - 1;

            for( $i=0; $i<$len; $i++) {
                $n = rand(0, $alphaLength);
                $pass[] = $alphabet[$n];
            }

            return implode($pass);
        }

        /**
         * _getRandomChar
         * @return string
         */
        protected function _getRandomChar() {
            $len            = 1;
            $alphabet       = "ABCDEFGHIJKLMNOPQRSTUWXYZ";
            $pass           = array();
            $alphaLength    = strlen($alphabet) - 1;

            for( $i=0; $i<$len; $i++) {
                $n = rand(0, $alphaLength);
                $pass[] = $alphabet[$n];
            }

            return implode($pass);
        }

        /**
         * setSharePassword
         *
         * @param string $password
         */
        public function setSharePassword($password=null) {
            if( $password == null ) {
                $password = $this->_getRandomPassword();
            }

            $this->_sharepassword = $password;
        }

        /**
         * getDefaultShares
         *
         * @return string
         */
        public function getDefaultShares() {
            $shares = array();

            // add privat folder
            $shares[] = array(
                'name' => $this->getUsername()
                );

            // add group folder
            $groups = elogin_bo::getEgroupwareAccountGroups($this->_egwaccountid);

            foreach( $groups as $groupname ) {
                $shares[] = array(
                    'name' => 'group ' . $groupname
                    );
            }

            return $shares;
        }

        /**
         * getShares
         *
         * @return array
         */
        public function getShares() {
            $shareprovider = $this->getProvider();
            $rshares = array();

            if( $shareprovider instanceof elogin_shareprovider_bo ) {
                $shares = $shareprovider->getSharesByUser($this);

                foreach( $shares as $tshare ) {
                    $add = false;

                    if( isset($tshare['is_readonly']) && $tshare['is_readonly'] ) {
                        $add = true;
                    }

                    if( isset($tshare['is_writable']) && $tshare['is_writable'] ) {
                        $add = true;
                    }

                    if( $add ) {
                        $rshares[] = $tshare;
                    }
                }

                // add drivename or mountpoint
                // TODO
            }

            return $rshares;
        }

        /**
         * updateUserSharesMounts
         *
         */
        public function updateUserSharesMounts() {
            $shares = $this->getShares();
        //var_dump($shares);
        //exit;
             $_randdrivename = array(
                'A', 'B', 'C', 'D', 'E', 'F'
                );

            foreach( $shares as $tshare ) {
                $mounts = elogin_usershares_mount_bo::getUserShareMountsBy(
                    $tshare['name'], $this->_id);

                if( count($mounts) == 0 ) {
                    $mountname = '';

                    $tmc = 0;

                    while( $trand = $this->_getRandomChar() ) {
                        $tmc++;

                        if( $tmc > 26 ) {
                            break;
                        }

                        if( in_array($trand, $_randdrivename) ) {
                            continue;
                        }

                        $_randdrivename[] = $trand;
                        $mountname = $trand;
                        break;
                    }

                    $mount = new elogin_usershares_mount_bo();
                    $mount->setUsershareId($this->_id);
                    $mount->setShareSource($tshare['name']);
                    $mount->setMountname($mountname);
                    $mount->save();
                }
            }
        }

        /**
         * getUserSharesMounts
         *
         * @return array of elogin_usershares_mount_bo
         */
        public function getUserSharesMounts() {
            if( $this->_id != null ) {
                return elogin_usershares_mount_bo::getUserShareMountsBy(
                    null,
                    $this->_id
                    );
            }

            return null;
        }

        /**
         * getCmds
         * @param string $system
         * @return array of elogin_cmd_bo
         */
        public function getCmds($system=null) {
            $cmds = array();

            $mounts = $this->getUserSharesMounts();

            if( $mounts ) {
                foreach( $mounts as $mount ) {
                    if( $mount instanceof elogin_usershares_mount_bo ) {
                        $cmds[] = $mount->getCmd($system);
                    }
                }
            }

            return $cmds;
        }

        /**
         * save
         */
        public function save() {
            $data = array();

            if( $this->_id ) {
                $data['el_unid'] = $this->_id;
            }

            $data['el_provider_id']     = $this->_providerid;
            $data['el_egw_account']     = $this->_egwaccountid;
            $data['el_sharepassword']   = $this->_sharepassword;
            $data['el_shareinfo']       = $this->_shareinfos;

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
                if( isset($query['col_filter']['provider_id']) ) {
                    $where['el_provider_id'] = $query['col_filter']['provider_id'];
                }

                if( isset($query['col_filter']['account_id']) ) {
                    $where['el_egw_account'] = $query['col_filter']['account_id'];
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
         * existByAccountAndProvider
         *
         * @param int $accountid
         * @param string $providerid
         * @return boolean
         */
        static public function existByAccountAndProvider($accountid, $providerid) {
            $query = array(
                'col_filter' => array(
                    'provider_id' => $providerid,
                    'account_id' => $accountid
                    )
                );
            $rows = array();
            $readonlys = array();

            self::get_rows($query, $rows, $readonlys);

            if( count($rows) > 0 ) {
                return $rows[0]['el_unid'];
            }

            return false;
        }

        /**
         * getAllByAccount
         *
         * @param type $accountid
         * @return array of elogin_usershares_bo
         */
        static public function getAllByAccount($accountid) {
            $query = array(
                'col_filter' => array(
                    'account_id' => $accountid
                    )
                );
            $rows = array();
            $readonlys = array();

            self::get_rows($query, $rows, $readonlys);

            $list = array();

            foreach( $rows as $row ) {
                $list[] = new elogin_usershares_bo($row['el_unid']);
            }

            return $list;
        }
    }

    /**
     * init_static
     */
    elogin_usershares_bo::init_static();