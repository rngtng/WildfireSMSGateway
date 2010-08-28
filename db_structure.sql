-- phpMyAdmin SQL Dump
-- version 2.11.10.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.2
-- Generation Time: Aug 28, 2010 at 04:43 PM
-- Server version: 4.1.22
-- PHP Version: 4.4.8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `db21560_16`
--

-- --------------------------------------------------------

--
-- Table structure for table `smsContacts`
--

CREATE TABLE `smsContacts` (
  `number` varchar(50) NOT NULL default '',
  `owner` varchar(50) NOT NULL default '',
  `name` varchar(50) NOT NULL default '',
  PRIMARY KEY  (`number`,`owner`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `smsMessages`
--

CREATE TABLE `smsMessages` (
  `no` int(10) unsigned NOT NULL auto_increment,
  `number` varchar(50) default NULL,
  `text` text,
  `owner` varchar(50) NOT NULL default '',
  `send` tinyint(3) unsigned NOT NULL default '0',
  `time` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`no`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=258 ;

-- --------------------------------------------------------

--
-- Table structure for table `smsUser`
--

CREATE TABLE `smsUser` (
  `id` varchar(100) NOT NULL default '',
  `name` varchar(100) NOT NULL default '',
  `language` varchar(100) NOT NULL default '',
  `number` varchar(20) NOT NULL default '',
  `credits` int(10) NOT NULL default '0',
  `username` varchar(25) NOT NULL default '',
  `password` varchar(25) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
