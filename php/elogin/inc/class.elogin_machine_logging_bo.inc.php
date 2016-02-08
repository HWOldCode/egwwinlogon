<?php

    /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-15 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

    /**
     * elogin_machine_logging_bo
     *
     */
    class elogin_machine_logging_bo {

        /**
         * TABLE
         */
        const TABLE = 'egw_elogin_machine_logging';

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
         * machine_id
         * @var string
         */
        protected $_machine_id = null;

        /**
         * egroupware userid
         * @var int
         */
        protected $_account_id = null;

        /**
         * Event
         * @var string
         */
        protected $_event = null;

        /**
         * level
         * @var int
         */
        protected $_level = null;

        /**
         * message
         * @var string
         */
        protected $_message = '';

        /**
         * timestamp of log date
         * @var int
         */
        protected $_logdate = null;

        /**
         * index
         * @var string
         */
        protected $_index = '';

        /**
         * Init our static properties
         */
        static public function init_static() {
            self::$_db = $GLOBALS['egw']->db;
        }

        /**
         * constructotr
         *
         * @param string $id
         */
        public function __construct($id=null) {
            if( $id ) {
                $data = self::read($id);

                if( $data ) {
                    $this->_machine_id  = $data['el_machine_id'];
                    $this->_account_id  = $data['el_account_id'];
                    $this->_event       = $data['el_event'];
                    $this->_logdate     = strtotime($data['el_logdate']);
                    $this->_message     = $data['el_message'];
                    $this->_index       = $data['el_index'];
                }

                $this->_id = $id;
            }
            else {
                $this->_account_id = $GLOBALS['egw_info']['user']['account_id'];
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
         * getAccountId
         *
         * @return int
         */
        public function getAccountId() {
            return $this->_account_id;
        }

        /**
         * getAccountName
         *
         * @return string
         */
        public function getAccountName() {
            return accounts::id2name($this->_account_id);
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
         * setMessage
         *
         * @param string $message
         */
        public function setMessage($message) {
            $this->_message = $message;
        }

        /**
         * getMessage
         *
         * @return string
         */
        public function getMessage() {
            return $this->_message;
        }

        /**
         * setLogDate
         *
         * @param int $timestamp
         */
        public function setLogDate($timestamp) {
            $this->_logdate = $timestamp;
        }

        /**
         * getLogDate
         *
         * @return int
         */
        public function getLogDate() {
            return $this->_logdate;
        }

        /**
         * getIndex
         *
         * @return string
         */
        public function getIndex() {
            return $this->_index;
        }

        /**
         * setIndex
         *
         * @param string $index
         */
        public function setIndex($index) {
            $this->_index = $index;
        }

        /**
         * save
         */
        public function save() {
            if( $this->_id == null ) {
                $data = array(
                    'el_machine_id' => $this->_machine_id,
                    'el_account_id' => $this->_account_id,
                    'el_event'      => $this->_event,
                    'el_logdate'    => date("Y-m-d H:i:s", $this->_logdate),
                    'el_message'    => $this->_message,
                    'el_index'      => $this->_index
                    );

                $return = self::_write($data);

                if( $return ) {
                    if( !($this->_id) ) {
                        $this->_id = $return;
                    }
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
         * @param array $query
         * @param array $rows
         * @param array $readonlys
         * @return int
         */
        static public function get_rows(&$query, &$rows, &$readonlys) {
            $where = array();
            $cols = array(self::TABLE . '.*');
            $join = '';

			if( isset($query['col_filter'])) {
				foreach( $query['col_filter'] as $filter ) {
					$where[] = $filter;
				}
			}

			if( !isset($query['start']) ) {
				$query['start'] = 0;
			}

			$start = ($query['num_rows'] ?
				array((int)$query['start'], $query['num_rows']) :
				(int)$query['start']);

			list($start, $num_rows) = $start;

			if( ($num_rows == null) || ($num_rows == false) ) {
				$num_rows = -1;
			}

			$order = 'el_logdate';

			if( isset($query['order']) ) {
				$order = $query['order'];
			}

			$sort = "ASC";

			if( isset($query['sort']) && ($query['sort'] == 'DESC') ) {
				$sort = "DESC";
			}

			$total = self::$_db->select(self::TABLE, 'COUNT(*)',
				$where, __LINE__, __FILE__, false, '', false, 0, $join)->fetchColumn();

            if( !($rs = self::$_db->select(self::TABLE, $cols, $where, __LINE__, __FILE__,
                $start, ' ORDER BY ' . self::TABLE . '.' . $order . ' ' .
				$sort . ', ' . self::TABLE . '.el_index ' . $sort, false, $num_rows, $join)) )
            {
                return array();
            }

            $rows = array();

            foreach( $rs as $row ) {
				$row = (array) $row;
                $rows[] = $row;
            }

            return $total;
        }

		/**
         * expression
         *
         * @param string $table
         * @param array $filter
         * @return string
         */
        static public function expression($table, $filter) {
            return self::$_db->expression($table, $filter);
        }

		/**
		 * getLastLogByMachineId
		 *
		 * @param string $id
		 * @return elogin_machine_logging_bo
		 */
		static public function getLastLogByMachineId($id) {
			$where = array(
				'el_machine_id' => $id
				);

            $cols = array(self::TABLE . '.el_unid');

			if( !($rs = self::$_db->select(self::TABLE, $cols, $where, __LINE__, __FILE__,
                0, ' ORDER BY ' . self::TABLE . '.el_logdate DESC', false, 1, '')) )
            {
                return null;
            }

			foreach( $rs as $row ) {
				$row = (array) $row;

				return new elogin_machine_logging_bo($row['el_unid']);
			}

			return null;
		}
    }

    /**
     * init
     */
    elogin_machine_logging_bo::init_static();