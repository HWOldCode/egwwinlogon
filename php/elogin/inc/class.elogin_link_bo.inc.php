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
	 * elogin_link_bo
	 */
	class elogin_link_bo {

		/**
         * TABLE
         */
        const TABLE = 'egw_elogin_link';

		/**
		 * OPTIONS
		 */
		const OPTION_OPEN_EXPLORER			= 'open_explorer';			// open in explorer
		const OPTION_OPEN_EXPLORER_SELECT	= 'open_explorer_select';	// open in explorer with selection
		const OPTION_TITLE_SHORT			= 'title_short';			// only file title
		const OPTION_LINKFORMAT_SMB			= 'linkformat_smb';			// linkformat in smb
		const OPTION_LINK_SHOW				= 'link_show';				// show the link in dialog

		/**
         * Reference to global db object
         * @var egw_db
         */
        static protected $_db;

        /**
         * id
         * @var string
         */
        protected $_id = null;

		/**
		 * usershare id
		 * @var string
		 */
		protected $_usershare_id = null;

		/**
		 * userhare mount id
		 * @var string
		 */
		protected $_usershare_mount_id = null;

		/**
		 * filepath
		 * @var string
		 */
		protected $_filepath = '';

		/**
		 * options
		 * @var string
		 */
		protected $_options = array();

		/**
         * Init our static properties
         */
        static public function init_static() {
            self::$_db = $GLOBALS['egw']->db;
        }

		/**
         * __construct
         * @param string $id
         */
        public function __construct($id=null) {
            if( $id != null ) {
                $data = self::read($id);

                if( $data ) {
					$this->_usershare_id		= $data['el_usershare_id'];
					$this->_usershare_mount_id  = $data['el_usershare_mount_id'];
					$this->_filepath			= $data['el_filepath'];
					$options					= $data['el_options'];

					if( $options != '' ) {
						$this->_options	= json_decode($options, true);
					}
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
		 * getUserShareId
		 * @return string
		 */
		public function getUserShareId() {
			return $this->_usershare_id;
		}

		/**
		 * setUserShareId
		 * @param string $id
		 */
		public function setUserShareId($id) {
			$this->_usershare_id = $id;
		}

		/**
		 * getUserShareMountId
		 * @return string
		 */
		public function getUserShareMountId() {
			return $this->_usershare_mount_id;
		}

		/**
		 * setUserShareMountId
		 * @param string $id
		 */
		public function setUserShareMountId($id) {
			$this->_usershare_mount_id = $id;
		}

		/**
		 * getFilePath
		 * @return string
		 */
		public function getFilePath() {
			return $this->_filepath;
		}

		/**
		 * setFilePath
		 * @param string $filepath
		 */
		public function setFilePath($filepath) {
			$this->_filepath = $filepath;
		}

		/**
		 * setOption
		 * @param string $name
		 * @param string $key
		 */
		public function setOption($name, $key) {
			$this->_options[$name] = $key;
		}

		/**
		 * getOption
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
		 * @return array
		 */
		public function getOptions() {
			return $this->_options;
		}

		/**
		 * buildUri
		 * @return string
		 */
		public function buildUri() {
			$usershare		= new elogin_usershares_bo($this->_usershare_id);
			$usersharemount = new elogin_usershares_mount_bo($this->_usershare_mount_id);

			if( $usersharemount->getUsershareId() == $usershare->getId() ) {
				$uri = $usersharemount->getMountname() . '://' . $this->_filepath;
			}
		}

		/**
         * save
         */
        public function save() {
            $data = array();

            if( $this->_id ) {
                $data['el_unid'] = $this->_id;
            }

			$data['el_usershare_id']		= $this->_usershare_id;
			$data['el_usershare_mount_id']	= $this->_usershare_mount_id;
			$data['el_filepath']			= $this->_filepath;

			if( is_array($this->_options) ) {
				$data['el_options'] = json_encode($this->_options);
			}

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
                /*if( isset($query['col_filter']['provider_id']) ) {
                    $where['el_provider_id'] = $query['col_filter']['provider_id'];
                }

                if( isset($query['col_filter']['account_id']) ) {
                    $where['el_egw_account'] = $query['col_filter']['account_id'];
                }*/
            }

            if( !($rs = self::$_db->select(self::TABLE, $cols, $where, __LINE__, __FILE__,
                false, '', false, -1, $join)) )
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
	}

	/**
     * init
     */
    elogin_link_bo::init_static();