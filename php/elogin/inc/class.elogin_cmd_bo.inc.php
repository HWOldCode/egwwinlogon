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
		const TYPE_WIN_STA0		= 'winsta0';

		// events
		const EVENT_NONE		= 'none';
        const EVENT_LOGIN_PRE   = 'login_pre';
        const EVENT_LOGIN       = 'login';
        const EVENT_LOGIN_AFTER = 'login_after';
		const EVENT_LOCK		= 'lock';
		const EVENT_UNLOCK		= 'unlock';
		const EVENT_LOGOFF		= 'logoff';

        // seperator
        const SEP_CONDITION = ',';

        // condition`s
        const CONDITION_WITH_CONSOLE    = 'with_console';
        const CONDITION_WAIT            = 'wait';
        const CONDITION_LOGGING         = 'logging';

		// scripts
		const SCRIPT_BATCHFILE	= 'batchfile';
		const SCRIPT_VBS			= 'vbs';

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
		 * name
		 * @var string
		 */
		protected $_name	= '';

		/**
		 * catid
		 * @var int
		 */
		protected $_catid	= 0;

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
         * @var array
         */
        protected $_condition = array();

		/**
		 * with options
		 *
		 * @var array
		 */
		protected $_options = array();

		/**
		 * script type
		 * @var string
		 */
		protected $_script_type = '';

		/**
		 * script
		 *
		 * @var string
		 */
		protected $_script = '';

		/**
		 * scheduler time
		 * @var int
		 */
		protected $_scheduler_time = 0;

		/**
		 * mount point check
		 * @var string
		 */
		protected $_mount_point_check = '';

        /**
         * Init our static properties
         */
        static public function init_static() {
            self::$_db = $GLOBALS['egw']->db;
        }

        /**
         * constructor
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
					$this->_name		= $data['el_name'];
					$this->_catid		= intval($data['el_catid']);
					$this->_options		= json_decode($data['el_options'], true);

					if( !is_array($this->_options) ) {
						$this->_options = array();
					}

					$this->_script_type			= $data['el_script_type'];
					$this->_script				= $data['el_script'];
                    $this->_condition			= explode(self::SEP_CONDITION, $data['el_condition']);
					$this->_scheduler_time		= $data['el_scheduler_time'];
					$this->_mount_point_check	= $data['el_mountpoint_check'];
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
		 * getName
		 *
		 * @return string
		 */
		public function getName() {
			return $this->_name;
		}

		/**
		 * setName
		 *
		 * @param string $name
		 */
		public function setName($name) {
			$this->_name = $name;
		}

		/**
		 * getCatId
		 *
		 * @return int
		 */
		public function getCatId() {
			return $this->_catid;
		}

		/**
		 * setCatId
		 *
		 * @param int $id
		 */
		public function setCatId($id) {
			$this->_catid = intval($id);
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
		 * setSchedulerTime
		 *
		 * @param int $sec
		 */
		public function setSchedulerTime($sec=0) {
			$this->_scheduler_time = intval($sec);
		}

		/**
		 * getSchedulerTime
		 *
		 * @return int
		 */
		public function getSchedulerTime() {
			return $this->_scheduler_time;
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
         * setCondition
         * @param array $condition
         */
        public function setCondition($condition) {
            if( is_array($condition) ) {
                $this->_condition = $condition;
            }
        }

        /**
         * getCondition
         * @return array
         */
        public function getCondition() {
            return $this->_condition;
        }

		/**
		 * setOption
		 *
		 * @param string $name
		 * @param string $key
		 */
		public function setOption($name, $key) {
			$this->_options[$name] = $key;
		}

		/**
		 * getOption
		 *
		 * @param string $name
		 * @return string|null
		 */
		public function getOption($name) {
			if( isset($this->_options[$name]) ) {
				return $this->_options[$name];
			}

			return null;
		}

		/**
		 * getOptions
		 *
		 * @return array
		 */
		public function getOptions() {
			return $this->_options;
		}

		/**
		 * getScriptType
		 *
		 * @return string
		 */
		public function getScriptType() {
			return $this->_script_type;
		}

		/**
		 * setScriptType
		 *
		 * @param string $type
		 */
		public function setScriptType($type) {
			$this->_script_type = $type;
		}

		/**
		 * setScript
		 *
		 * @param string $script
		 */
		public function setScript($script) {
			$this->_script = $script;
		}

		/**
		 * getScript
		 *
		 * @return string
		 */
		public function getScript() {
			return $this->_script;
		}

		/**
		 * getMountPointCheck
		 * @return string
		 */
		public function getMountPointCheck() {
			return $this->_mount_point_check;
		}

		/**
		 * setMountPointCheck
		 * @param string $mp
		 */
		public function setMountPointCheck($mp) {
			$this->_mount_point_check = $mp;
		}

		/**
         * save
         */
        public function save() {
            $data = array();

            if( $this->_id ) {
                $data['el_unid'] = $this->_id;
            }

			if( $this->_account_id == null ) {
				$this->_account_id = '';
			}

            $data['el_machine_id']			= $this->_machine_id;
            $data['el_account_id']			= $this->_account_id;
            $data['el_command']				= $this->_command;
            $data['el_system']				= $this->_system;
            $data['el_order']				= $this->_order;
            $data['el_type']				= $this->_type;
            $data['el_event']				= $this->_event;
            $data['el_condition']			= implode(
                self::SEP_CONDITION, $this->_condition);

			$data['el_name']				= $this->_name;
			$data['el_catid']				= $this->_catid;
			$data['el_options']				= json_encode($this->_options);
			$data['el_script_type']			= $this->_script_type;
			$data['el_script']				= $this->_script;
			$data['el_scheduler_time']		= $this->_scheduler_time;
			$data['el_mountpoint_check']	= $this->_mount_point_check;

            $return = self::_write($data);

            if( $return ) {
                if( !($this->_id) ) {
                    $this->_id = $return;
                }
            }
        }

		/**
		 * delete
		 */
		public function delete() {
			if( $this->_id != null ) {
				self::_delete($this->_id);
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
		 * _delete
		 * @param string $id
		 */
		static protected function _delete($id) {
			self::$_db->delete(
				self::TABLE,
				array(
					'el_unid' => $id,
					),
				__LINE__,
				__FILE__,
				'elogin'
				);
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

			$total = self::$_db->select(self::TABLE, 'COUNT(*)',
				$where, __LINE__, __FILE__, false, '', false, 0, $join)->fetchColumn();

            if (!($rs = self::$_db->select(self::TABLE, $cols, $where, __LINE__, __FILE__,
                $start, '', false, $num_rows, $join)))
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
                'id'				=> $this->_id,
                'machine_id'		=> $this->_machine_id,
                'account_id'		=> strval($this->_account_id),
                'command'			=> $this->_command,
                'system'			=> $this->_system,
                'order'				=> strval($this->_order),
                'type'				=> $this->_type,
                'event'				=> $this->_event,
                'condition'			=> $this->_condition,
				'name'				=> $this->_name,
				'options'			=> $this->_options,
				'script_type'		=> $this->_script_type,
				'script'			=> $this->_script,
				'catid'				=> $this->_catid,
				'catname'			=> categories::id2name($this->_catid),
				'scheduler_time'	=> $this->_scheduler_time,
				'mount_point_check'	=> $this->_mount_point_check,
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

		/**
		 * link_title
		 *
		 * @param type $info
		 * @return string
		 */
		static public function link_title($info) {
			$cmd = new elogin_cmd_bo($info);

			if( $cmd->getMachineId() != null ) {
				return $cmd->getName();
			}

			return lang('not found');
		}

		/**
		 * link_titles
		 *
		 * @param array $ids
		 */
		static public function link_titles(array $ids) {
            $titles = array();

            foreach( $ids as $id ) {
                $titles[$id] = self::link_title($id);
            }

            return $titles;
		}

		/**
         * link_query
         *
         * @param type $pattern
         * @param array $options
         */
        static public function link_query($pattern, Array &$options = array()) {
			$rows		= array();
			$readonlys	= array();
			$result = array();

			if( self::get_rows($options, $rows, $readonlys) > 0 ) {
				foreach( $rows as &$row ) {
					$result[$row['el_unid']] = array(
							'label' => $row['el_name'],
							);
				}

				return $result;
			}

			return array();
		}
    }

    /**
     * init
     */
    elogin_cmd_bo::init_static();