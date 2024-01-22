insert into roles(name_role)
values ('ADMIN');
insert into roles(name_role)
values ('USER');

insert into users (login, password, username)
values ('admin', 'admin', 'admin');
insert into users (login, password, username)
values ('user', 'user', 'user');

insert into user_role(user_id, role_id)
values (9, 1);
insert into user_role(user_id, role_id)
values (10, 2);

select r.name_role
from roles r
         join user_role ur on r.role_id = ur.role_id
         join users u on ur.user_id = u.user_id
where u.username = ?;