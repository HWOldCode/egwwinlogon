<?php

    /**
	 * ELogin - Egroupware
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-17 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

	use EGroupware\Eworkflow;

	require_once('class.elogin_action_share_provider_shares_base.inc.php');

    /**
     * elogin_action_share_provider_dir_permission_remove
     */
    class elogin_action_share_provider_dir_permission_remove extends elogin_action_share_provider_shares_base implements eworkflow_ientry_bo, eworkflow_iparam_bo {

        // link action
		const LINK_ACTION      = 'action';
		const LINK_ERROR       = 'error';

        // Param
        const PARAM_DPR_DIRNAME	= 'dpr_dirname';

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
         * dirname
         * @var string
         */
        protected $_dirname = "";

        /**
         * getEntryDefaultIcon
         * @return string
         */
        public function getEntryDefaultIcon() {
            return "permissionremove.png";
        }

        /**
		 * getEtemplate
		 * @return null|etemplate|string
		 */
		public function getEtemplate() {
			return 'entry.action_egw_elogin_shareprovider_dir_permission_remove';
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
				'title' => lang('Action EGW ELogin Share Provider dir permission remove'),
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
				$this->_dirname, static::PARAM_DPR_DIRNAME);
        }

		/**
		 * setDirname
		 * @param string $dirname
		 */
		public function setDirname($dirname) {
			$this->_dirname = $this->_params->saveVariableToParam(
				$dirname, static::PARAM_DPR_DIRNAME);
		}

        /**
		 * uiEdit
		 * @param array $content
		 */
		public function uiEdit(&$content, &$option_sel, &$readonlys) {
            if( isset($content['button']) && isset($content['button']['save']) ) {
                $this->setUserShareEntryid($content['usershare_entry']);
                $this->setDirname($content['dirname']);
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
                Eworkflow\Widget\Ptextbox::createOptions(
                    $this->getGroupEntryId(),
                    $this->getId(),
                    array(
                        'onlyPlaceholder' => true,
                        'searchByTree' => true,
                        )
                    );

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
            if( !$this->_setStart($params) ) { return; }

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
                $pro->getParamValue(static::PARAM_DPR_DIRNAME));

            $entryid = $this->getUserShareEntryid();
            $entry = eworkflow_entrys_bo::loadEntry($entryid);

            if( $entry instanceof elogin_action_share_provider_shares ) {
                $provider = $entry->getProvider();
                $sharename = $entry->getShareName();

				if( $pcl == '1' ) {
					$provider->setUseCacheLogging(true);
				}

                $this::$_logger->info('UserShare: ' . $sharename);

				try {
					if( $provider->removeAllPermissionDir("/" . $sharename . '/', $dirname) ) {
						$linkname = self::LINK_ACTION;
						$this::$_logger->info('Dir remove permission in UserShare: ' . "/" . $sharename . '/' . $dirname);
					}
					else {
						$linkname = self::LINK_ERROR;
						$this::$_logger->severe('Dir can`t remove permission in UserShare: ' . "/" . $sharename . '/' . $dirname);
					}
				}
				catch( Exception $ex ) {
					$this::$_logger->severe('Error: ' . $ex->getMessage());
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
