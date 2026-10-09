USE [master]
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = N'courseweb_db')
    DROP DATABASE [courseweb_db]
GO

CREATE DATABASE [courseweb_db]
GO

USE [courseweb_db]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- ==========================================
-- 1. BẢNG USERS 
-- ==========================================
CREATE TABLE [dbo].[users](
	[id] [varchar](36) NOT NULL,
	[username] [varchar](50) NOT NULL,
	[full_name] [nvarchar](100) NULL,
	[email] [varchar](50) NOT NULL,
	[password] [varchar](255) NOT NULL,
	[phone] [varchar](10) NULL,
	[role] [varchar](20) NOT NULL DEFAULT 'STUDENT',
	[created_at] [datetime2](7) NULL DEFAULT getdate(),
	[updated_at] [datetime2](7) NULL DEFAULT getdate(),
PRIMARY KEY CLUSTERED ([id] ASC),
UNIQUE NONCLUSTERED ([email] ASC),
UNIQUE NONCLUSTERED ([username] ASC)
)
GO

-- ==========================================
-- 2. BẢNG COURSE_CATEGORIES
-- ==========================================
CREATE TABLE [dbo].[course_categories](
	[id] [varchar](36) NOT NULL,
	[name] [nvarchar](100) NOT NULL,
	[description] [nvarchar](max) NULL,
PRIMARY KEY CLUSTERED ([id] ASC)
)
GO

-- ==========================================
-- 3. BẢNG COURSES
-- ==========================================
CREATE TABLE [dbo].[courses](
	[id] [varchar](36) NOT NULL,
	[teacher_id] [varchar](36) NOT NULL,
	[category_id] [varchar](36) NOT NULL,
	[title] [nvarchar](255) NOT NULL,
	[description] [nvarchar](max) NULL,
	[thumbnail_url] [varchar](255) NULL,
	[price] [decimal](10, 2) NOT NULL DEFAULT 0.00,
	[status] [varchar](20) NULL DEFAULT 'DRAFT',
	[created_at] [datetime2](7) NULL DEFAULT getdate(),
	[updated_at] [datetime2](7) NULL DEFAULT getdate(),
PRIMARY KEY CLUSTERED ([id] ASC),
FOREIGN KEY([category_id]) REFERENCES [dbo].[course_categories] ([id]),
-- KHÔNG CÀI CASCADE ở đây để bảo vệ khóa học khi vô tình xóa Giảng viên
FOREIGN KEY([teacher_id]) REFERENCES [dbo].[users] ([id]), 
CHECK (([status]='ARCHIVED' OR [status]='PUBLISHED' OR [status]='DRAFT'))
)
GO

-- ==========================================
-- 4. BẢNG ENROLLMENTS
-- ==========================================
CREATE TABLE [dbo].[enrollments](
	[id] [varchar](36) NOT NULL,
	[student_id] [varchar](36) NOT NULL,
	[course_id] [varchar](36) NOT NULL,
	[status] [varchar](20) NULL DEFAULT 'ACTIVE',
	[enroll_date] [datetime2](7) NULL DEFAULT getdate(),
PRIMARY KEY CLUSTERED ([id] ASC),
UNIQUE NONCLUSTERED ([student_id] ASC, [course_id] ASC),
-- Thêm ON DELETE CASCADE: Xóa khóa học -> bay danh sách ghi danh
FOREIGN KEY([course_id]) REFERENCES [dbo].[courses] ([id]) ON DELETE CASCADE,
-- Thêm ON DELETE CASCADE: Xóa học viên -> bay danh sách ghi danh
FOREIGN KEY([student_id]) REFERENCES [dbo].[users] ([id]) ON DELETE CASCADE,
CHECK (([status]='COMPLETED' OR [status]='CANCELED' OR [status]='ACTIVE'))
)
GO

-- ==========================================
-- 5. BẢNG LESSONS 
-- ==========================================
CREATE TABLE [dbo].[lessons](
	[id] [varchar](36) NOT NULL,
	[course_id] [varchar](36) NOT NULL,
	[lesson_order] [int] NOT NULL,
	[name] [nvarchar](255) NOT NULL,
	[assignment_url] [nvarchar](1000) NULL, 
	[created_at] [datetime2](7) NULL DEFAULT getdate(),
	[updated_at] [datetime2](7) NULL DEFAULT getdate(),
PRIMARY KEY CLUSTERED ([id] ASC),
-- ON DELETE CASCADE: Xóa khóa học -> bay danh sách bài học
FOREIGN KEY([course_id]) REFERENCES [dbo].[courses] ([id]) ON DELETE CASCADE
)
GO

-- ==========================================
-- 6. BẢNG VIDEO_LESSONS 
-- ==========================================
CREATE TABLE [dbo].[video_lessons](
	[lesson_id] [varchar](36) NOT NULL,
	[url] [varchar](255) NOT NULL,
	[duration_seconds] [int] NOT NULL,
PRIMARY KEY CLUSTERED ([lesson_id] ASC),
-- ON DELETE CASCADE: Xóa bài học -> bay video đính kèm
FOREIGN KEY([lesson_id]) REFERENCES [dbo].[lessons] ([id]) ON DELETE CASCADE
)
GO

-- ==========================================
-- 7. BẢNG LESSON_PROGRESS
-- ==========================================
CREATE TABLE [dbo].[lesson_progress](
	[id] [varchar](36) NOT NULL,
	[student_id] [varchar](36) NOT NULL,
	[lesson_id] [varchar](36) NOT NULL,
	[watched_time_seconds] [int] NULL DEFAULT 0,
	[is_completed] [bit] NULL DEFAULT 0,
	[last_watched_at] [datetime2](7) NULL DEFAULT getdate(),
	[last_position] [int] NULL,
PRIMARY KEY CLUSTERED ([id] ASC),
UNIQUE NONCLUSTERED ([student_id] ASC, [lesson_id] ASC),
-- Thêm ON DELETE CASCADE: Xóa bài học -> bay tiến độ của bài đó
FOREIGN KEY([lesson_id]) REFERENCES [dbo].[lessons] ([id]) ON DELETE CASCADE,
-- Thêm ON DELETE CASCADE: Xóa học viên -> bay tiến độ học của người đó
FOREIGN KEY([student_id]) REFERENCES [dbo].[users] ([id]) ON DELETE CASCADE
)
GO