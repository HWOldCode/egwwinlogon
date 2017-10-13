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

/**
 * elogin_handler_bo
 */
class elogin_handler_bo {

	/**
	 * logging
	 * @var boolean
	 */
	static protected $_logging = false;

	/**
	 * set_async_job
	 * @param boolean $start
	 * @param elogin_handler_bo $handler
	 * @return boolean|null
	 */
	static public function setAsyncJob($start=true, $handler) {
		$class = null;

		if( $handler instanceof elogin_handler_bo ) {
			$class = get_class($handler);
		}

		if( $class == null ) {
			return null;
		}


		$async = new asyncservice();

		if( $start === !$async->read('elogin-' . $class) ) {
			if( $start ) {
				$async->set_timer(
					array('hour' => '*'),
					'elogin-' . $class,
					'elogin.' . $class . '.handle',
					null
					);

				return true;
			}
			else {
				$async->cancel_timer('elogin-' . $class);
				return false;
			}
		}

		return null;
	}

	/**
	 * handle
	 * @throws Exception
	 */
	static public function handle() {
		throw new Exception('Override this static methode!');
	}

	/**
	 * _openNewProcessFile
	 * @param string $handler
	 * @return boolean
	 */
	static protected function _openNewProcessFile($handler) {
		$pf = $GLOBALS['egw_info']['server']['temp_dir'] . '/elogin_' . $handler . '.tmp';

		$cache_life = 60 * 60 * 6;
		$return = false;

		if( file_exists($pf) ) {
			$filemtime = @filemtime($pf);

			if( (!$filemtime) || (time() - $filemtime >= $cache_life)) {
				unlink($pf);
				$return = true;
			}
		}
		else {
			$return = true;
		}

		if( $return ) {
			file_put_contents($pf, '1');
		}

		return $return;
	}

	/**
	 * _closeProcessFile
	 * @param string $handler
	 */
	static protected function _closeProcessFile($handler) {
		$pf = $GLOBALS['egw_info']['server']['temp_dir'] . '/elogin_' . $handler . '.tmp';

		if( file_exists($pf) ) {
			@unlink($pf);
		}
	}

	static protected function _getOpenProcessFiles() {

	}

	/**
	 * _errorLog
	 * @param string $message
	 * @param int $line
	 */
	static protected function _errorLog($message, $line) {
		if( !self::$_logging ) {
			return;
		}

		if( is_array($message) ) {
			$message = var_export($message, true);
		}

		$message = 'Line: ' . $line . ' Message: ' . $message . "\r\n";

		$file = $GLOBALS['egw_info']['server']['temp_dir'] . '/elogin_sharehandler.log';

		error_log($message, 3, $file);
	}
}