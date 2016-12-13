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

	require_once('class.elogin_action_share_provider_shares_base.inc.php');

    /**
     * elogin_action_share_provider_dir_permission_set
     */
    class elogin_action_share_provider_dir_permission_set extends elogin_action_share_provider_shares_base implements eworkflow_ientry_bo, eworkflow_iparam_bo {

        // link action
		const LINK_ACTION      = 'action';
		const LINK_ERROR       = 'error';

        // Param
        const PARAM_DPS_DIRNAME             = 'dps_dirname';
        const PARAM_DPS_USERNAME            = 'dps_username';
        const PARAM_DPS_READ                = 'dps_read';
        const PARAM_DPS_WRITE               = 'dps_write';

        /**
         * logger
         * @var CEcomanLogger
         */
        static protected $_logger = null;

        /**
         * type
         * @var string
         */
        protected $_type = CEcomanWorkflowEntry::TYPE_ACTION;

        /**
         * usershare entry id
         * @var string
         */
        protected $_usershare_entryid = "";

        /**
         * dirname
         * @var string
         */
        protected $_dirname = "";

        /**
         * username
         * @var string
         */
        protected $_username = "";

        /**
         *
         * @var string
         */
        protected $_dps_read = "";

        /**
         *
         * @var string
         */
        protected $_dps_write = "";

        /**
         * getEntryDefaultIcon
         * @return string
         */
        public function getEntryDefaultIcon() {
            return "permissionadd.png";
        }

        /**
		 * getEtemplate
		 * @return null|etemplate|string
		 */
		public function getEtemplate() {
			return 'entry.action_egw_elogin_shareprovider_dir_permission_set';
		}

        /**
         * acceptLinks
         * accept links
         * @return array
         */
        public function acceptLinks() {
            return array(
				static::LINK_ACTION,
                static::LINK_ERROR
                );
        }

        /**
		 * getInfo
		 * @return array
		 */
		static public function getInfo() {
			return array(
				'title' => lang('Action EGW ELogin Share Provider dir permission set'),
				'type' => self::TYPE_ACTION,
				'class' => static::_getClassName(self),
				'category' => array(
                    'ELogin',
					),
				);
		}

        /**
         * getDirname
         * @return string
         */
        public function getDirname() {
			return $this->_params->getVariableByParam(
				$this->_dirname, static::PARAM_DPS_DIRNAME);
        }

		/**
		 * setDirname
		 * @param string $dirname
		 */
		public function setDirname($dirname) {
			$this->_dirname = $this->_params->saveVariableToParam(
				$dirname, static::PARAM_DPS_DIRNAME);
		}

        /**
         * getUsername
         * @return string
         */
        public function getUsername() {
			return $this->_params->getVariableByParam(
				$this->_username, static::PARAM_DPS_USERNAME);
        }

		/**
		 * setUsername
		 * @param string $username
		 */
		public function setUsername($username) {
			$this->_username = $this->_params->saveVariableToParam(
				$username, static::PARAM_DPS_USERNAME);
		}

        /**
		 * uiEdit
		 * @param array $content
		 */
		public function uiEdit(&$content, &$option_sel, &$readonlys) {
            if( isset($content['button']) && isset($content['button']['save']) ) {
                $this->setUserShareEntryid($content['usershare_entry']);
                $this->setDirname($content['dirname']);
                $this->setUsername($content['username']);
				$this->setProviderCacheLogging($content['cache_logging']);
            }

            $content['usershare_entry'] = $this->getUserShareEntryid();
            $option_sel['usershare_entry'] = array();

            $group = new CEcomanWorkflowEntryGroup($this->getGroupEntryId());
            $path = new eworkflow_entry_path_bo($group);

            // -----------------------------------------------------------------
            $fentry = $path->findEntryO($this,
                'elogin_action_share_provider_shares');

            while( $fentry instanceof CEcomanWorkflowEntry ) {
                $entry = eworkflow_entrys_bo::loadEntry($fentry->getId());

                if( $entry instanceof elogin_action_share_provider_shares ) {
                    $description = $entry->getDescription();

                    $option_sel['usershare_entry'][$entry->getId()] =
                        ($description == '' ? 'Title Empty' : $description);

                    $fentry = $path->findEntryO($entry,
                        'elogin_action_share_provider_shares');
                }
                else {
                    break;
                }
            }

            // -----------------------------------------------------------------
            $content['dirname'] = $this->getDirname();
            $content['options-dirname'] =
                eworkflow_ptextbox_etemplate_widget::createOptions(
                    $this->getGroupEntryId(),
                    $this->getId(),
                    array(
                        'onlyPlaceholder' => true,
                        'searchByTree' => true,
                        )
                    );

            $content['username'] =
                eworkflow_dialog_input_etemplate_widget::setSettingToValue(
					$this->getUsername(),
					array(
					));

			// -----------------------------------------------------------------

			$content['cache_logging'] = $this->getProviderCacheLogging();

			// -----------------------------------------------------------------

            parent::uiEdit($content, $option_sel, $readonlys);
        }

        /**
         * execute
         * @param type $params
         * @return type
         */
        public function execute($params) {
            if( !$this->_setStart($params) ) { return; };

            // params merge
            // -----------------------------------------------------------------
			$ppo = new eworkflow_process_param_bo(
				$params,
				$this->getParamList(),
				$this->_entryParameter());

            $pro = $this->getParameterRegister();
            $pro->setProcessParam($ppo);

			$params = $ppo->getParams();

            // -----------------------------------------------------------------

			// provider cache logging
			$pcl = $this->getProviderCacheLogging();

            // -----------------------------------------------------------------

            $linkname = self::LINK_ERROR;
            $dirname   = eworkflow_vfs_bo::cleanUtf8PathName(
                $pro->getParamValue(static::PARAM_DPS_DIRNAME));

            $username =
                eworkflow_dialog_input_etemplate_widget::getEWorkflowValueBy(
                    $this->getUsername(),
                    $pro
                    );

            $entryid = $this->getUserShareEntryid();
            $entry = eworkflow_entrys_bo::loadEntry($entryid);

            if( $entry instanceof elogin_action_share_provider_shares ) {
                $provider = $entry->getProvider();
                $sharename = $entry->getShareName();

				if( $pcl == '1' ) {
					$provider->setUseCacheLogging(true);
				}

                $this::$_logger->info('UserShare: "' . $sharename . '"');

                if( $provider->addPermissionDir("/" . $sharename . '/', $dirname, $username, true, true) ) {
                    $linkname = self::LINK_ACTION;

                    $this::$_logger->info(
						'Dir set permission in UserShare: ' . "/" .
						$sharename . '/' . $dirname .
						' username: ' . $username);
                }
                else {
                    $linkname = self::LINK_ERROR;

                    $this::$_logger->severe(
						'Dir can`t set permission in UserShare: ' . "/" .
						$sharename . '/' . $dirname .
						' username: ' . $username);
                }

				if( $pcl == '1' ) {
					$this::$_logger->info('Cache Logging: ' . var_export($provider->getCacheLogs(), true));
					$provider->setUseCacheLogging(false);
				}
            }

            // get link for next action
            $this->_execNextEntryByLinkName($linkname, $params);
        }

        /**
         * getParameterRegister
         * @return eworkflow_param_register
         */
        public function getParameterRegister() {
            $reg = parent::getParameterRegister();

            return $reg;
        }
    }
