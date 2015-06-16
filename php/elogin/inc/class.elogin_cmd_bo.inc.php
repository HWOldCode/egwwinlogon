<?php

    /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-15 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id:$
	 */

    /**
     * elogin_cmd_bo
     */
    class elogin_cmd_bo {

        /**
         * TABLE
         */
        const TABLE = 'egw_elogin_cmd';

        // types
        const TYPE_USER         = 'user';
        const TYPE_SERVICE      = 'service';

        const EVENT_LOGIN_PRE   = 'login_pre';
        const EVENT_LOGIN       = 'login';
        const EVENT_LOGIN_AFTER = 'login_after';

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
         * command
         * @var string
         */
        protected $_command = '';

        /**
         * system
         * @var string
         */
        protected $_system = elogin_bo::SYSTEM_WIN;

        /**
         * order
         * @var int
         */
        protected $_order = 0;

        /**
         * type
         * @var string
         */
        protected $_type = elogin_cmd_bo::TYPE_USER;

        /**
         * event
         * @var string
         */
        protected $_event = elogin_cmd_bo::EVENT_LOGIN;

        /**
         * condition
         * @var string
         */
        protected $_condition = '';

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
                    $this->_machine_id  = $data['el_machine_id'];
                    $this->_account_id  = $data['el_account_id'];
                    $this->_command     = $data['el_command'];
                    $this->_system      = $data['el_system'];
                    $this->_order       = $data['el_order'];
                    $this->_type        = $data['el_type'];
                    $this->_event       = $data['el_event'];
                    // TODO
                }
            }

            $this->_id = $id;
        }

        /**
         * getId
         * @return string
         */
        public function getId() {
            return $this->_id;
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
         * setAccountId
         * @param string $id
         */
        public function setAccountId($id) {
            $this->_account_id = $id;
        }

        /**
         * getAccountId
         *
         * @return string
         */
        public function getAccountId() {
            return $this->_account_id;
        }

        /**
         * setCommand
         *
         * @param string $command
         */
        public function setCommand($command) {
            $this->_command = $command;
        }

        /**
         * getCommand
         *
         * @return string
         */
        public function getCommand() {
            return $this->_command;
        }

        /**
         * setSystem
         *
         * @param string $system
         */
        public function setSystem($system) {
            $this->_system = $system;
        }

        /**
         * getSystem
         *
         * @return string
         */
        public function getSystem() {
            return $this->_system;
        }

        /**
         * setOrder
         *
         * @param int $order
         */
        public function setOrder($order=0) {
            $this->_order = $order;
        }

        /**
         * getOrder
         *
         * @return int
         */
        public function getOrder() {
            return $this->_order;
        }

        /**
         * setType
         *
         * @param string $type
         */
        public function setType($type) {
            $this->_type = $type;
        }

        /**
         * getType
         *
         * @return string
         */
        public function getType() {
            return $this->_type;
        }

        /**
         * setEvent
         *
         * @param string $event
         */
        public function setEvent($event) {
            $this->_event = $event;
        }

        /**
         * getEvent
         *
         * @return string
         */
        public function getEvent() {
            return $this->_event;
        }

        /**
         * save
         */
        public function save() {
            $data = array();

            if( $this->_id ) {
                $data['el_unid'] = $this->_id;
            }

            $data['el_machine_id']  = $this->_machine_id;
            $data['el_account_id']  = $this->_account_id;
            $data['el_command']     = $this->_command;
            $data['el_system']      = $this->_system;
            $data['el_order']       = $this->_order;
            $data['el_type']        = $this->_type;
            $data['el_event']       = $this->_event;
            // TODO

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
                if( isset($query['col_filter']['machine_id']) ) {
                    $where['el_machine_id'] = $query['col_filter']['machine_id'];
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
         * getAllByMachineId
         *
         * @param type $machineid
         * @return array of elogin_cmd_bo
         */
        static public function getAllByMachineId($machineid) {
            $query = array(
                'col_filter' => array(
                    'machine_id' => $machineid,
                    )
                );

            $rows = array();
            $readonlys = array();

            self::get_rows($query, $rows, $readonlys);

            $list = array();

            foreach( $rows as $row ) {
                $list[] = new elogin_cmd_bo($row['el_unid']);
            }

            return $list;
        }

        /**
         * toArray
         * @return array
         */
        public function toArray() {
            return array(
                'id'            => $this->_id,
                'machine_id'    => $this->_machine_id,
                'account_id'    => $this->_account_id,
                'command'       => $this->_command,
                'system'        => $this->_system,
                'order'         => $this->_order,
                'type'          => $this->_type,
                'event'         => $this->_event
                );
        }

        /**
         * toJson
         *
         * @return string
         */
        public function toJson() {
            return json_encode($this->toArray());
        }
    }

    /**
     * init
     */
    elogin_cmd_bo::init_static();