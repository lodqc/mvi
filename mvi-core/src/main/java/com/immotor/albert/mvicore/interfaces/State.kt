package com.immotor.albert.mvicore.interfaces

/**
 * UI的状态它始终具有一个值
 * view状态viewModel->view
 */
interface  State

/**
 * 没有默认值，满足条件才触发的事件
 * view事件viewModel->view
 */
interface Event

/**
 * UI的意图view->viewModel
 */
interface Action