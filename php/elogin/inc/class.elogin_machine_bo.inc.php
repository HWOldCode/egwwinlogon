<?php

    /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-14 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

    /**
     * elogin_machine_bo
     */
    class elogin_machine_bo {

        /**
         * TABLE
         */
        const TABLE = 'egw_elogin_machine';

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
         * in db saved?
         * @var boolean
         */
        protected $_inDb = false;

        /**
         * name
         * @var string
         */
        protected $_name = '';

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
        public function __construct($id) {
            $this->_id = $id;

            $data = $this->read($this->_id);

            if( $data ) {
                $this->_inDb = true;
                $this->_name = $data['el_name'];
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
         * getIsInDb
         *
         * @return boolean
         */
        public function getIsInDb() {
            return $this->_inDb;
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
         * getName
         *
         * @return string
         */
        public function getName() {
            return $this->_name;
        }

        /**
         * save
         */
        public function save() {
            $data = array();

            if( $this->_id ) {
                $data['el_unid'] = $this->_id;
            }

            $data['el_name'] = $this->_name;

            $return = self::_write($data, $this->_inDb);

            if( $return ) {
                if( !($this->_id) ) {
                    $this->_id = $return;
                }
            }
        }

        /**
         * createNewLogging
         *
         * @return elogin_machine_logging_bo
         */
        public function createNewLogging() {
            $log = new elogin_machine_logging_bo();
            $log->setMachineId($this->_id);

            return $log;
        }

        /**
         * getCurrentUserShares
         *
         * @return array of elogin_usershares_bo
         */
        public function getCurrentUserShares() {
            $account_id = $GLOBALS['egw_info']['user']['account_id'];
            $usershares = elogin_usershares_bo::getAllByAccount($account_id);

            return $usershares;
        }

        /**
         * getCmds
         * @return array of elogin_cmd_bo
         */
        public function getCmds() {
            $list = elogin_cmd_bo::getAllByMachineId($this->_id);
            $list_all = elogin_cmd_bo::getAllByMachineId('all');

            foreach( $list_all as $entry ) {
                $list[] = $entry;
            }

            return $list;
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
         * _write
         *
         * @param array $data
         */
        static protected function _write(array $data, $inDb) {
            if( isset($data['el_unid']) && $inDb ) {
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
                if( !isset($data['el_unid']) ) {
                    $data['el_unid'] = elogin_bo::getPHPUuid();
                }

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
		 * link_title
		 *
		 * @param type $info
		 * @return string
		 */
		static public function link_title($info) {
			$cmd = new elogin_machine_bo($info);
			
			if( $cmd->getIsInDb() ) {
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
        static public function link_query($pattern, Array &$options=array()) {
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
     * init_static
     */
    elogin_machine_bo::init_static();