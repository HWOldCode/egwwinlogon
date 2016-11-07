<?php

	/**
	 * Syndms
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package syno
	 * @copyright (c) 2012-16 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

	/**
	 * SyndmsClient6
	 */
	class SyndmsClient6 extends SyndmsClientBase {

		// consts
		const VERSION_DSM_6	= '6';

		const URL_ENCRYPTION = 'encryption.cgi';

		/**
		 * encryption
		 * @var array|null
		 */
		protected $_encryption = null;

		/**
         * _createUrl
         * @param string $url
         * @return string
         */
        protected function _createUrl($url) {
			$url = sprintf($url, '');

			return parent::_createUrl($url);
		}

		/**
         * _initServices
         */
        public function _initServices() {
			parent::_initServices();
		}

		/**
		 * _loadEncryption
		 */
		public function _loadEncryption() {
			$data = $this->_queryByService('SYNO.API.Encryption', array(
				'format'	=> 'module',
				'api'		=> 'SYNO.API.Encryption',
				'method'	=> 'getinfo',
				'version'	=> '1'
				), true);

			if( $data !== null) {
				if( is_array($data) ) {
					$this->_encryption = $data;
				}
			}
		}

		/**
         * _initConnection
         * @return boolean
         */
        public function _initConnection() {
			$response = $this->_request(self::URL_INDEX);

			$this->_loadEncryption();

			if( $this->_encryption != null ) {
				$this->_initConnection = true;
				return true;
			}

			$this->_initConnection = false;
            return false;
        }

		/**
		 * getFileSharesList
		 * @param string $sharename
		 * @param int $limit
		 * @param array $options
		 * @return array
		 */
        public function getFileSharesList($sharename, $limit=1000, $options=array()) {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.FileStation.List', array(
                    'method'            => 'list',
                    'version'           => '2',
                    'folder_path'       => '"' . $this->_escapeFolderName($sharename) . '"',
                    'filetype'          => '"all"',
                    'additional'        => '["real_path","size","owner","time,perm","type","mount_point_type"]',
                    'action'            => 'list',
                    'sort_direction'    => '"ASC"',
                    'sort_by'           => '"name"',
                    'sort_by'           => 0,
                    'limit'             => $limit,
                    ), true);

                if( $data && is_array($data) && isset($data['files']) ) {
                    $files = (array) $data['files'];
                    $rlist = array();

                    foreach( $files as $file ) {
                        $tfile = (array) $file;

                        if( isset($options['only_dir']) && ($options['only_dir'] == true) ) {
                            if( $tfile['isdir'] == true ) {
                                $rlist[$tfile['path']] = $tfile['name'];
                            }
                        }
                        elseif( isset($options['only_file']) && ($options['only_file'] == true) ) {
                            if( $tfile['isdir'] == false ) {
                                $rlist[$tfile['path']] = $tfile['name'];
                            }
                        }
                        else {
                            $rlist[$tfile['path']] = $tfile['name'];
                        }
                    }

                    return $rlist;
                }
            }

            return array();
        }

		/**
         * createDirShare
         * @param string $sharename
         * @param string $dir
         * @return boolean
         */
        public function createDirShare($sharename, $dir) {
            if( $this->_isLogin ) {
                $data = $this->_queryByService('SYNO.FileStation.CreateFolder', array(
                    'method'            => 'create',
                    'version'           => '2',
                    'folder_path'       => '"' . $this->_escapeFolderName($sharename) . '"',
                    'name'              => '"' . $this->_escapeFolderName($dir) . '"',
                    'force_parent'      => true,
                    ), true);

                if( $data && is_array($data) && isset($data['folders']) ) {
                    $folders = (array) $data['folders'];

                    foreach( $folders as $tfolder ) {
                        $folder = (array) $tfolder;

                        if( $folder['name'] == $dir ) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
	}