#!/usr/bin/env perl
if (!$::Driver) { use FindBin; exec("$FindBin::Bin/bootstrap.pl", @ARGV, $0); die; }
# DESCRIPTION: Verilator: Verilog Test driver/expect definition
#
# Copyright 2003 by Wilson Snyder. This program is free software; you
# can redistribute it and/or modify it under the terms of either the GNU
# Lesser General Public License Version 3 or the Perl Artistic License
# Version 2.0.
# SPDX-License-Identifier: LGPL-3.0-only OR Artistic-2.0

scenarios(vltmt => 1);

# It doesn't really matter what test
top_filename("t/t_gen_alw.v");

compile(
    v_flags2 => ["--prof-threads --threads 2"]
    );

execute(
    all_run_flags => ["+verilator+prof+threads+start+0",
                      " +verilator+prof+threads+file+/dev/null",
                      " +verilator+prof+vlt+file+$Self->{obj_dir}/profile.vlt",
                      ],
    check_finished => 1,
    );

file_grep("$Self->{obj_dir}/profile.vlt", qr/profile_data/i);

compile(
    # Intentinally no --prof-threads here, so we make sure profile data
    # can read in without it (that is no prof-thread effect on profile_data hash names)
    v_flags2 => ["--threads 2",
                 " $Self->{obj_dir}/profile.vlt"],
    );

execute(
    check_finished => 1,
    );

ok(1);
1;
